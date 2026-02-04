package com.snackstore.service;

import com.snackstore.common.BizException;
import com.snackstore.entity.Address;
import com.snackstore.entity.Cart;
import com.snackstore.entity.CartItem;
import com.snackstore.entity.Order;
import com.snackstore.entity.OrderItem;
import com.snackstore.entity.Product;
import com.snackstore.entity.Shipment;
import com.snackstore.repository.AddressRepository;
import com.snackstore.repository.CartItemRepository;
import com.snackstore.repository.CartRepository;
import com.snackstore.repository.OrderItemRepository;
import com.snackstore.repository.OrderRepository;
import com.snackstore.repository.ProductRepository;
import com.snackstore.repository.ShipmentRepository;
import com.snackstore.util.ImagePlaceholderUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ShipmentRepository shipmentRepository;
  private final AddressRepository addressRepository;
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;

  public OrderService(
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      ShipmentRepository shipmentRepository,
      AddressRepository addressRepository,
      CartRepository cartRepository,
      CartItemRepository cartItemRepository,
      ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.shipmentRepository = shipmentRepository;
    this.addressRepository = addressRepository;
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }

  public List<OrderView> listUserOrders(Long userId) {
    List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    List<OrderView> views = new ArrayList<>();
    for (Order o : orders) {
      views.add(buildOrderView(o));
    }
    return views;
  }

  public OrderView getUserOrder(Long userId, Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> BizException.notFound("订单不存在"));
    if (!order.getUserId().equals(userId)) throw BizException.forbidden("无权限");
    return buildOrderView(order);
  }

  @Transactional
  public OrderView createFromCart(Long userId, Long addressId) {
    if (addressId == null) throw BizException.badRequest("地址ID不能为空");
    Address address =
        addressRepository.findById(addressId).orElseThrow(() -> BizException.notFound("地址不存在"));
    if (!address.getUserId().equals(userId)) throw BizException.forbidden("无权限");

    Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> BizException.badRequest("购物车为空"));
    List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
    if (items.isEmpty()) throw BizException.badRequest("购物车为空");

    BigDecimal amount = BigDecimal.ZERO;
    List<OrderItem> orderItems = new ArrayList<>();

    for (CartItem ci : items) {
      Product product =
          productRepository.findById(ci.getProductId()).orElseThrow(() -> BizException.notFound("商品不存在"));
      if (!"上架".equals(product.getStatus())) throw BizException.badRequest("商品已下架：" + product.getName());
      int stock = product.getStock() == null ? 0 : product.getStock();
      if (stock <= 0) throw BizException.badRequest("商品缺货：" + product.getName());
      int qty = Math.min(ci.getQuantity(), stock);
      if (qty <= 0) throw BizException.badRequest("商品缺货：" + product.getName());

      BigDecimal price = product.getPrice();
      amount = amount.add(price.multiply(BigDecimal.valueOf(qty)));

      OrderItem oi = new OrderItem();
      oi.setProductId(product.getId());
      oi.setProductName(product.getName());
      oi.setProductImage(resolveSnapshotImage(product.getId(), product.getName(), product.getMainImage()));
      oi.setPrice(price);
      oi.setQuantity(qty);
      orderItems.add(oi);

      product.setStock(stock - qty);
      productRepository.save(product);
    }

    LocalDateTime now = LocalDateTime.now();
    Order order = new Order();
    order.setOrderNo(genOrderNo());
    order.setUserId(userId);
    order.setAmount(amount);
    order.setStatus("待发货");
    order.setReceiverName(address.getName());
    order.setReceiverPhone(address.getPhone());
    order.setReceiverRegion(address.getRegion());
    order.setReceiverDetail(address.getDetail());
    order.setCreatedAt(now);
    order.setUpdatedAt(now);
    Order saved = orderRepository.save(order);

    for (OrderItem oi : orderItems) {
      oi.setOrderId(saved.getId());
      orderItemRepository.save(oi);
    }

    cartItemRepository.deleteAll(items);
    cart.setUpdatedAt(now);
    cartRepository.save(cart);

    return buildOrderView(saved);
  }

  @Transactional
  public void cancelUserOrder(Long userId, Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> BizException.notFound("订单不存在"));
    if (!order.getUserId().equals(userId)) throw BizException.forbidden("无权限");
    if ("已取消".equals(order.getStatus()) || "已完成".equals(order.getStatus())) return;
    if (!"待发货".equals(order.getStatus())) throw BizException.badRequest("当前状态不可取消");

    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
    for (OrderItem item : items) {
      Product product = productRepository.findById(item.getProductId()).orElse(null);
      if (product != null && product.getStock() != null) {
        product.setStock(product.getStock() + item.getQuantity());
        productRepository.save(product);
      }
    }

    order.setStatus("已取消");
    order.setUpdatedAt(LocalDateTime.now());
    orderRepository.save(order);
  }

  @Transactional
  public void confirmUserOrder(Long userId, Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> BizException.notFound("订单不存在"));
    if (!order.getUserId().equals(userId)) throw BizException.forbidden("无权限");
    if (!"待收货".equals(order.getStatus())) throw BizException.badRequest("当前状态不可确认收货");
    order.setStatus("已完成");
    order.setUpdatedAt(LocalDateTime.now());
    orderRepository.save(order);
  }

  public List<OrderView> listAllOrders() {
    List<Order> orders = orderRepository.findAll();
    List<OrderView> views = new ArrayList<>();
    for (Order o : orders) {
      views.add(buildOrderView(o));
    }
    return views;
  }

  @Transactional
  public void shipOrder(Long orderId, String company, String trackingNo) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> BizException.notFound("订单不存在"));
    if (!"待发货".equals(order.getStatus())) throw BizException.badRequest("当前状态不可发货");
    order.setStatus("待收货");
    order.setUpdatedAt(LocalDateTime.now());
    orderRepository.save(order);

    Shipment shipment = shipmentRepository.findByOrderId(order.getId()).orElseGet(Shipment::new);
    shipment.setOrderId(order.getId());
    shipment.setCompany(company);
    shipment.setTrackingNo(trackingNo);
    shipment.setShippedAt(LocalDateTime.now());
    shipmentRepository.save(shipment);
  }

  @Transactional
  public void updateOrderStatus(Long orderId, String status) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> BizException.notFound("订单不存在"));
    order.setStatus(status);
    order.setUpdatedAt(LocalDateTime.now());
    orderRepository.save(order);
  }

  private OrderView buildOrderView(Order order) {
    OrderView view = new OrderView();
    view.setOrder(order);
    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
    for (OrderItem item : items) {
      item.setProductImage(
          resolveSnapshotImage(item.getProductId(), item.getProductName(), item.getProductImage()));
    }
    view.setItems(items);
    Optional<Shipment> shipment = shipmentRepository.findByOrderId(order.getId());
    view.setShipment(shipment.orElse(null));
    return view;
  }

  private String resolveSnapshotImage(Long productId, String productName, String url) {
    if (url == null || url.trim().isEmpty() || url.startsWith("/images/")) {
      return ImagePlaceholderUtil.buildSvgDataUrl(
          productName,
          "零食商场 · 订单",
          pickColorA(productId, 1),
          pickColorB(productId, 1));
    }
    return url;
  }

  private String pickColorA(Long seed, int offset) {
    String[] colors = {"#7C3AED", "#A78BFA", "#4C1D95", "#22C55E", "#FF1493", "#FFAA00", "#00FFFF"};
    int idx = (int) ((seed == null ? 0 : seed) + offset) % colors.length;
    return colors[idx];
  }

  private String pickColorB(Long seed, int offset) {
    String[] colors = {"#A78BFA", "#7C3AED", "#22C55E", "#FFAA00", "#00FFFF", "#FF1493", "#4C1D95"};
    int idx = (int) ((seed == null ? 0 : seed) + offset) % colors.length;
    return colors[idx];
  }

  private String genOrderNo() {
    String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
    Random random = new Random();
    for (int i = 0; i < 5; i++) {
      String suffix = String.format("%06d", random.nextInt(1_000_000));
      String orderNo = "O" + date + suffix;
      if (orderRepository.findByOrderNo(orderNo) == null) return orderNo;
    }
    return "O" + date + System.currentTimeMillis();
  }

  public static class OrderView {
    private Order order;
    private List<OrderItem> items;
    private Shipment shipment;

    public Order getOrder() {
      return order;
    }

    public void setOrder(Order order) {
      this.order = order;
    }

    public List<OrderItem> getItems() {
      return items;
    }

    public void setItems(List<OrderItem> items) {
      this.items = items;
    }

    public Shipment getShipment() {
      return shipment;
    }

    public void setShipment(Shipment shipment) {
      this.shipment = shipment;
    }
  }
}

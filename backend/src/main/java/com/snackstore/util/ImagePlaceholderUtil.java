package com.snackstore.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ImagePlaceholderUtil {
  private ImagePlaceholderUtil() {}

  public static String buildSvgDataUrl(String title, String subtitle, String colorA, String colorB) {
    String safeTitle = String.valueOf(title);
    if (safeTitle.length() > 18) safeTitle = safeTitle.substring(0, 18);
    String safeSubtitle = String.valueOf(subtitle);
    if (safeSubtitle.length() > 28) safeSubtitle = safeSubtitle.substring(0, 28);
    String svg =
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"960\" height=\"640\" viewBox=\"0 0 960 640\"><defs><linearGradient id=\"g\" x1=\"0\" y1=\"0\" x2=\"1\" y2=\"1\"><stop offset=\"0\" stop-color=\""
            + colorA
            + "\"/><stop offset=\"1\" stop-color=\""
            + colorB
            + "\"/></linearGradient></defs><rect width=\"960\" height=\"640\" rx=\"36\" fill=\"url(#g)\"/><circle cx=\"780\" cy=\"140\" r=\"120\" fill=\"rgba(255,255,255,0.18)\"/><circle cx=\"180\" cy=\"520\" r=\"160\" fill=\"rgba(255,255,255,0.14)\"/><text x=\"64\" y=\"250\" font-size=\"54\" font-family=\"Rubik, Nunito Sans, system-ui\" fill=\"rgba(255,255,255,0.96)\" font-weight=\"700\">"
            + escapeXml(safeTitle)
            + "</text><text x=\"64\" y=\"320\" font-size=\"28\" font-family=\"Nunito Sans, system-ui\" fill=\"rgba(255,255,255,0.9)\">"
            + escapeXml(safeSubtitle)
            + "</text><text x=\"64\" y=\"520\" font-size=\"22\" font-family=\"Nunito Sans, system-ui\" fill=\"rgba(255,255,255,0.75)\">零食商场 · Backend</text></svg>";
    try {
      String encoded = URLEncoder.encode(svg, StandardCharsets.UTF_8.name()).replace("+", "%20");
      return "data:image/svg+xml;charset=UTF-8," + encoded;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static String escapeXml(String s) {
    return String.valueOf(s)
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }
}

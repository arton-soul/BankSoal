package tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GenerateStoreAssets {

    public static void main(String[] args) throws Exception {
        File outputDir = new File("store_assets");
        File screenshotsDir = new File("store_assets/screenshots");
        outputDir.mkdirs();
        screenshotsDir.mkdirs();

        System.out.println("Generating App Icon (512x512)...");
        generateAppIcon(new File(outputDir, "icon_512x512.png"));

        System.out.println("Generating Feature Graphic (1024x500)...");
        generateFeatureGraphic(new File(outputDir, "feature_graphic_1024x500.png"));

        System.out.println("Generating Screenshot 1 - Home (1080x1920)...");
        generateScreenshot1(new File(screenshotsDir, "screenshot_1_home.png"));

        System.out.println("Generating Screenshot 2 - Exam CAT (1080x1920)...");
        generateScreenshot2(new File(screenshotsDir, "screenshot_2_exam_cat.png"));

        System.out.println("Generating Screenshot 3 - Result Analytics (1080x1920)...");
        generateScreenshot3(new File(screenshotsDir, "screenshot_3_result.png"));

        System.out.println("Generating Screenshot 4 - Review & KaTeX (1080x1920)...");
        generateScreenshot4(new File(screenshotsDir, "screenshot_4_review.png"));

        System.out.println("All store visual assets generated successfully!");
    }

    private static void setupQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    // ==========================================
    // 1. APP ICON 512x512 (No transparency for Play Store)
    // ==========================================
    private static void generateAppIcon(File file) throws Exception {
        int width = 512;
        int height = 512;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Rich Gradient Background
        GradientPaint bgPaint = new GradientPaint(
                0, 0, new Color(13, 71, 161),       // Deep Navy
                512, 512, new Color(25, 118, 210)   // Vivid Royal Blue
        );
        g.setPaint(bgPaint);
        g.fillRect(0, 0, width, height);

        // Subtle Radial Highlight on Top Left
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(180, 140), 280f,
                new float[]{0f, 1f},
                new Color[]{new Color(66, 165, 245, 120), new Color(13, 71, 161, 0)}
        );
        g.setPaint(glow);
        g.fillOval(0, 0, width, height);

        // Inner Shield / Soft Card
        RoundRectangle2D card = new RoundRectangle2D.Double(44, 44, 424, 424, 80, 80);
        g.setColor(new Color(255, 255, 255, 22));
        g.fill(card);
        g.setColor(new Color(255, 255, 255, 60));
        g.setStroke(new BasicStroke(3f));
        g.draw(card);

        // --- DRAW OPEN BOOK ---
        // Left Page
        Path2D.Double leftPage = new Path2D.Double();
        leftPage.moveTo(256, 180);
        leftPage.curveTo(220, 168, 160, 168, 120, 178);
        leftPage.lineTo(120, 318);
        leftPage.curveTo(160, 308, 220, 308, 256, 320);
        leftPage.closePath();

        g.setColor(new Color(248, 250, 252));
        g.fill(leftPage);
        g.setColor(new Color(203, 213, 225));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(leftPage);

        // Right Page
        Path2D.Double rightPage = new Path2D.Double();
        rightPage.moveTo(256, 180);
        rightPage.curveTo(292, 168, 352, 168, 392, 178);
        rightPage.lineTo(392, 318);
        rightPage.curveTo(352, 308, 292, 308, 256, 320);
        rightPage.closePath();

        g.setColor(new Color(255, 255, 255));
        g.fill(rightPage);
        g.setColor(new Color(203, 213, 225));
        g.draw(rightPage);

        // Book lines (Text hints on left page)
        g.setColor(new Color(148, 163, 184));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(150, 210, 230, 206);
        g.drawLine(150, 234, 230, 230);
        g.drawLine(150, 258, 230, 254);
        g.drawLine(150, 282, 210, 279);

        // Book lines on right page
        g.drawLine(282, 206, 362, 210);
        g.drawLine(282, 230, 362, 234);
        g.drawLine(282, 254, 362, 258);
        g.drawLine(282, 279, 342, 282);

        // Bookmark Ribbon (Red/Coral)
        Path2D.Double ribbon = new Path2D.Double();
        ribbon.moveTo(252, 178);
        ribbon.lineTo(260, 178);
        ribbon.lineTo(260, 340);
        ribbon.lineTo(256, 334);
        ribbon.lineTo(252, 340);
        ribbon.closePath();
        g.setColor(new Color(239, 68, 68));
        g.fill(ribbon);

        // --- GRADUATION CAP ON TOP ---
        Path2D.Double capTop = new Path2D.Double();
        capTop.moveTo(256, 108);
        capTop.lineTo(336, 134);
        capTop.lineTo(256, 160);
        capTop.lineTo(176, 134);
        capTop.closePath();
        g.setColor(new Color(30, 41, 59));
        g.fill(capTop);
        g.setColor(new Color(255, 213, 79));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(capTop);

        // Cap Skull
        Path2D.Double capBase = new Path2D.Double();
        capBase.moveTo(216, 147);
        capBase.lineTo(216, 166);
        capBase.curveTo(236, 176, 276, 176, 296, 166);
        capBase.lineTo(296, 147);
        g.setColor(new Color(15, 23, 42));
        g.fill(capBase);

        // Cap Tassel (Golden)
        g.setColor(new Color(255, 213, 79));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(256, 134, 318, 150);
        g.drawLine(318, 150, 322, 172);
        g.fillOval(318, 170, 7, 7);

        // --- PRO BADGE AT BOTTOM ---
        RoundRectangle2D proBadge = new RoundRectangle2D.Double(186, 370, 140, 52, 26, 26);
        GradientPaint goldGrad = new GradientPaint(
                186, 370, new Color(255, 213, 79),
                326, 422, new Color(255, 160, 0)
        );
        g.setPaint(goldGrad);
        g.fill(proBadge);

        // Inner shadow on badge
        g.setColor(new Color(255, 255, 255, 140));
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(188, 372, 136, 48, 24, 24));

        g.setColor(new Color(30, 27, 75));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth("PRO");
        g.drawString("PRO", 186 + (140 - textW) / 2, 407);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // ==========================================
    // 2. FEATURE GRAPHIC 1024x500
    // ==========================================
    private static void generateFeatureGraphic(File file) throws Exception {
        int width = 1024;
        int height = 500;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Deep Navy Tech Gradient Background
        GradientPaint bg = new GradientPaint(
                0, 0, new Color(10, 25, 47),
                1024, 500, new Color(23, 42, 69)
        );
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);

        // Subtle Diagonal Glow
        g.setPaint(new RadialGradientPaint(
                new Point2D.Float(800, 250), 450f,
                new float[]{0f, 1f},
                new Color[]{new Color(25, 118, 210, 90), new Color(10, 25, 47, 0)}
        ));
        g.fillRect(0, 0, width, height);

        // Grid lines accent on right
        g.setColor(new Color(255, 255, 255, 10));
        g.setStroke(new BasicStroke(1.5f));
        for (int x = 600; x < width; x += 40) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += 40) {
            g.drawLine(600, y, width, y);
        }

        // --- LEFT TEXT CONTENT ---
        // Pill: "SIMULASI CAT & BANK SOAL"
        RoundRectangle2D tagPill = new RoundRectangle2D.Double(60, 55, 270, 36, 18, 18);
        g.setColor(new Color(30, 58, 138));
        g.fill(tagPill);
        g.setColor(new Color(96, 165, 250));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(tagPill);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(147, 197, 253));
        g.drawString("[*] SIMULASI CAT & BANK SOAL", 76, 78);

        // Title: "Bank Soal Pro"
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.setColor(Color.WHITE);
        g.drawString("Bank Soal", 60, 145);
        int bw = g.getFontMetrics().stringWidth("Bank Soal ");
        GradientPaint gold = new GradientPaint(
                60 + bw, 100, new Color(255, 213, 79),
                60 + bw + 120, 150, new Color(255, 160, 0)
        );
        g.setPaint(gold);
        g.drawString("Pro", 60 + bw, 145);

        // Subtitle
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(226, 232, 240));
        g.drawString("Persiapan Terlengkap CPNS, UTBK & Kedinasan", 60, 185);

        // Category Pills
        String[] categories = {
                "CPNS SKD (TWK / TIU / TKP)",
                "UTBK - SNBT & TPS",
                "Sekolah Kedinasan",
                "Asesmen SMA & SMP"
        };
        int catY = 225;
        for (String cat : categories) {
            // Checkmark bullet
            g.setColor(new Color(34, 197, 94));
            g.fillOval(62, catY - 12, 16, 16);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("V", 66, catY);

            g.setFont(new Font("Arial", Font.PLAIN, 15));
            g.setColor(new Color(241, 245, 249));
            g.drawString(cat, 88, catY);
            catY += 32;
        }

        // Feature Highlights Bar at bottom left
        RoundRectangle2D featBar = new RoundRectangle2D.Double(60, 375, 490, 58, 14, 14);
        g.setColor(new Color(255, 255, 255, 18));
        g.fill(featBar);
        g.setColor(new Color(255, 255, 255, 40));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(featBar);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 213, 79));
        g.drawString("OPSI ACAK DINAMIS", 78, 410);
        g.setColor(new Color(203, 213, 225));
        g.drawString("|", 258, 410);
        g.setColor(new Color(56, 189, 248));
        g.drawString("OFFLINE READY", 276, 410);
        g.setColor(new Color(203, 213, 225));
        g.drawString("|", 412, 410);
        g.setColor(new Color(167, 139, 250));
        g.drawString("RUMUS KATEX", 428, 410);

        // --- RIGHT SIDE: PHONE MOCKUP CARD ---
        int cardX = 640;
        int cardY = 40;
        int cardW = 320;
        int cardH = 420;

        // Shadow behind card
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(cardX + 12, cardY + 12, cardW, cardH, 32, 32);

        // Card Container (White phone UI)
        g.setColor(new Color(248, 250, 252));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 32, 32);
        g.setColor(new Color(203, 213, 225));
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 32, 32);

        // Top App Bar
        g.setColor(new Color(13, 71, 161));
        g.fillRoundRect(cardX, cardY, cardW, 64, 32, 32);
        g.fillRect(cardX, cardY + 32, cardW, 32);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString("Simulasi CAT CPNS 2026", cardX + 18, cardY + 38);

        // Timer badge
        RoundRectangle2D timerBadge = new RoundRectangle2D.Double(cardX + cardW - 100, cardY + 18, 85, 28, 14, 14);
        g.setColor(new Color(220, 38, 38));
        g.fill(timerBadge);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("TIMER 85:40", cardX + cardW - 95, cardY + 37);

        // Question content inside mockup
        g.setColor(new Color(100, 116, 139));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("SOAL NO. 24 / 110 (TIU)", cardX + 20, cardY + 95);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString("Jika x = 25% dari 80, dan y = 3/4", cardX + 20, cardY + 125);
        g.drawString("dari 28, maka hubungan x dan y...", cardX + 20, cardY + 145);

        // Mockup Option Radios
        drawMockOption(g, cardX + 18, cardY + 175, cardW - 36, "A", "x > y", false);
        drawMockOption(g, cardX + 18, cardY + 225, cardW - 36, "B", "x < y  (x = 20, y = 21)", true); // Selected
        drawMockOption(g, cardX + 18, cardY + 275, cardW - 36, "C", "x = y", false);
        drawMockOption(g, cardX + 18, cardY + 325, cardW - 36, "D", "Hubungan tidak dapat ditentukan", false);

        // Passing Grade Badge overlay on phone
        RoundRectangle2D pgBadge = new RoundRectangle2D.Double(cardX + 25, cardY + 375, cardW - 50, 36, 18, 18);
        g.setColor(new Color(22, 101, 52));
        g.fill(pgBadge);
        g.setColor(new Color(134, 239, 172));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(pgBadge);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.WHITE);
        g.drawString("Lolos Passing Grade BKN", cardX + 72, cardY + 398);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void drawMockOption(Graphics2D g, int x, int y, int w, String label, String text, boolean selected) {
        RoundRectangle2D optCard = new RoundRectangle2D.Double(x, y, w, 40, 10, 10);
        if (selected) {
            g.setColor(new Color(238, 242, 255));
            g.fill(optCard);
            g.setColor(new Color(79, 70, 229));
            g.setStroke(new BasicStroke(2f));
            g.draw(optCard);
            // Selected Radio
            g.setColor(new Color(79, 70, 229));
            g.fillOval(x + 10, y + 10, 20, 20);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString(label, x + 16, y + 24);
            g.setColor(new Color(49, 46, 129));
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(text, x + 38, y + 25);
        } else {
            g.setColor(Color.WHITE);
            g.fill(optCard);
            g.setColor(new Color(226, 232, 240));
            g.setStroke(new BasicStroke(1f));
            g.draw(optCard);
            // Unselected Radio
            g.setColor(new Color(241, 245, 249));
            g.fillOval(x + 10, y + 10, 20, 20);
            g.setColor(new Color(71, 85, 105));
            g.drawOval(x + 10, y + 10, 20, 20);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString(label, x + 16, y + 24);
            g.setColor(new Color(71, 85, 105));
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(text, x + 38, y + 25);
        }
    }

    // ==========================================
    // 3. SCREENSHOT 1 - HOME & KATALOG (1080x1920)
    // ==========================================
    private static void generateScreenshot1(File file) throws Exception {
        int width = 1080;
        int height = 1920;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Header Background
        GradientPaint topBg = new GradientPaint(0, 0, new Color(13, 71, 161), 0, 480, new Color(25, 118, 210));
        g.setPaint(topBg);
        g.fillRect(0, 0, width, 480);

        // App Marketing Headline on Top
        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.setColor(Color.WHITE);
        g.drawString("Katalog Bank Soal Terlengkap", 80, 140);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.setColor(new Color(224, 231, 255));
        g.drawString("Simulasi Mandiri & Tryout CAT Nasional 2026", 80, 200);

        // Category Switcher / Tab Bar
        RoundRectangle2D tab = new RoundRectangle2D.Double(80, 260, 920, 80, 40, 40);
        g.setColor(new Color(255, 255, 255, 30));
        g.fill(tab);

        drawCategoryChip(g, 90, 270, "Semua", true);
        drawCategoryChip(g, 260, 270, "CPNS SKD", false);
        drawCategoryChip(g, 470, 270, "UTBK-SNBT", false);
        drawCategoryChip(g, 720, 270, "Kedinasan", false);

        // Body Canvas
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 400, width, height - 400);

        // Featured Card (CPNS SKD Master 2026)
        drawPackageCard(g, 80, 440, 920, 300,
                "Simulasi Resmi SKD CPNS 2026",
                "KemenPAN-RB & BKN - TWK, TIU, TKP",
                "110 Soal", "100 Menit", "Passing Grade: 311",
                new Color(13, 71, 161), true
        );

        // Card 2: UTBK-SNBT
        drawPackageCard(g, 80, 780, 920, 300,
                "Paket Master UTBK-SNBT 2026",
                "Tes Potensi Skolastik (TPS) & Literasi",
                "155 Soal", "195 Menit", "Skor Target: 700+",
                new Color(15, 118, 110), false
        );

        // Card 3: Sekolah Kedinasan (IPDN / STAN / STIS)
        drawPackageCard(g, 80, 1120, 920, 300,
                "Tryout Akbar Sekolah Kedinasan",
                "SKD Kedinasan - STAN, IPDN, STIS, POLTEKIP",
                "110 Soal", "100 Menit", "Passing Grade: 320",
                new Color(124, 45, 18), false
        );

        // Card 4: Asesmen SMA Matematika IPA
        drawPackageCard(g, 80, 1460, 920, 300,
                "Bank Soal Matematika & IPA SMA",
                "Kurikulum Merdeka & Ujian Sekolah",
                "40 Soal", "90 Menit", "Formula Standar",
                new Color(30, 64, 175), false
        );

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void drawCategoryChip(Graphics2D g, int x, int y, String label, boolean selected) {
        RoundRectangle2D chip = new RoundRectangle2D.Double(x, y, 150, 60, 30, 30);
        if (selected) {
            g.setColor(Color.WHITE);
            g.fill(chip);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(new Color(13, 71, 161));
            g.drawString(label, x + 30, y + 40);
        } else {
            g.setColor(new Color(255, 255, 255, 40));
            g.fill(chip);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            g.drawString(label, x + 25, y + 40);
        }
    }

    private static void drawPackageCard(Graphics2D g, int x, int y, int w, int h,
                                       String title, String sub, String meta1, String meta2, String meta3,
                                       Color accent, boolean isHot) {
        RoundRectangle2D card = new RoundRectangle2D.Double(x, y, w, h, 24, 24);
        g.setColor(Color.WHITE);
        g.fill(card);
        g.setColor(new Color(226, 232, 240));
        g.setStroke(new BasicStroke(2f));
        g.draw(card);

        // Left accent bar
        g.setColor(accent);
        g.fillRoundRect(x, y, 16, h, 24, 24);
        g.fillRect(x + 8, y, 8, h);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(15, 23, 42));
        g.drawString(title, x + 40, y + 65);

        // Subtitle
        g.setFont(new Font("Arial", Font.PLAIN, 26));
        g.setColor(new Color(100, 116, 139));
        g.drawString(sub, x + 40, y + 115);

        // Meta chips
        drawMetaChip(g, x + 40, y + 155, meta1);
        drawMetaChip(g, x + 240, y + 155, meta2);
        drawMetaChip(g, x + 460, y + 155, meta3);

        // Action Button: "Mulai Tryout"
        RoundRectangle2D btn = new RoundRectangle2D.Double(x + w - 240, y + 195, 200, 75, 20, 20);
        g.setColor(accent);
        g.fill(btn);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Mulai Ujian", x + w - 210, y + 242);

        if (isHot) {
            RoundRectangle2D hotBadge = new RoundRectangle2D.Double(x + w - 160, y + 35, 120, 44, 22, 22);
            g.setColor(new Color(239, 68, 68));
            g.fill(hotBadge);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("POPULER", x + w - 146, y + 65);
        }
    }

    private static void drawMetaChip(Graphics2D g, int x, int y, String text) {
        RoundRectangle2D chip = new RoundRectangle2D.Double(x, y, 180, 50, 16, 16);
        g.setColor(new Color(241, 245, 249));
        g.fill(chip);
        g.setColor(new Color(71, 85, 105));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(text, x + 16, y + 34);
    }

    // ==========================================
    // 4. SCREENSHOT 2 - EXAM CAT REALISTIS (1080x1920)
    // ==========================================
    private static void generateScreenshot2(File file) throws Exception {
        int width = 1080;
        int height = 1920;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Background
        g.setColor(new Color(241, 245, 249));
        g.fillRect(0, 0, width, height);

        // Header Top Bar
        GradientPaint hBg = new GradientPaint(0, 0, new Color(15, 23, 42), 0, 220, new Color(30, 41, 59));
        g.setPaint(hBg);
        g.fillRect(0, 0, width, 220);

        g.setFont(new Font("Arial", Font.BOLD, 46));
        g.setColor(Color.WHITE);
        g.drawString("Simulasi CAT Anti-Hafal", 60, 110);
        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.setColor(new Color(148, 163, 184));
        g.drawString("Opsi Jawaban Diacak Secara Dinamis Tiap Sesi", 60, 165);

        // Timer & Progress Header Strip
        RoundRectangle2D pStrip = new RoundRectangle2D.Double(60, 250, 960, 120, 24, 24);
        g.setColor(Color.WHITE);
        g.fill(pStrip);
        g.setColor(new Color(226, 232, 240));
        g.draw(pStrip);

        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(new Color(30, 41, 59));
        g.drawString("Soal No. 18 / 110", 90, 320);

        // Timer Chip
        RoundRectangle2D tChip = new RoundRectangle2D.Double(760, 275, 230, 70, 35, 35);
        g.setColor(new Color(254, 242, 242));
        g.fill(tChip);
        g.setColor(new Color(239, 68, 68));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(tChip);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("WAKTU 78:15", 790, 320);

        // Question Card
        RoundRectangle2D qCard = new RoundRectangle2D.Double(60, 400, 960, 400, 24, 24);
        g.setColor(Color.WHITE);
        g.fill(qCard);
        g.setColor(new Color(226, 232, 240));
        g.draw(qCard);

        // Subtest pill
        RoundRectangle2D subtestPill = new RoundRectangle2D.Double(90, 430, 200, 45, 12, 12);
        g.setColor(new Color(238, 242, 255));
        g.fill(subtestPill);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(new Color(79, 70, 229));
        g.drawString("TIU - Analogi Kata", 110, 462);

        // Question Text
        g.setFont(new Font("Arial", Font.PLAIN, 34));
        g.setColor(new Color(15, 23, 42));
        g.drawString("KUMAN : ANTISEPTIK = RUSA : ...", 90, 540);
        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.setColor(new Color(100, 116, 139));
        g.drawString("Pilihlah pasangan kata yang memiliki relasi analogi yang", 90, 610);
        g.drawString("paling setara dan logis dengan premis di atas.", 90, 660);

        // Dynamic Options (A, B, C, D, E)
        drawExamOption(g, 60, 830, 960, 110, "A", "Harimau", false);
        drawExamOption(g, 60, 960, 960, 110, "B", "Senapan (Antiseptik membasmi kuman, senapan membasmi rusa)", false);
        drawExamOption(g, 60, 1090, 960, 110, "C", "Kandang", true); // Selected
        drawExamOption(g, 60, 1220, 960, 110, "D", "Hutan Tropis", false);
        drawExamOption(g, 60, 1350, 960, 110, "E", "Rumput Segar", false);

        // Bottom Navigation Bar (Prev, Ragu-ragu, Next, Lembar Jawab)
        RoundRectangle2D bottomNav = new RoundRectangle2D.Double(60, 1680, 960, 140, 28, 28);
        g.setColor(Color.WHITE);
        g.fill(bottomNav);
        g.setColor(new Color(226, 232, 240));
        g.draw(bottomNav);

        drawExamNavBtn(g, 90, 1715, 200, 70, "<< Sebelumnya", new Color(100, 116, 139));
        drawExamNavBtn(g, 330, 1715, 200, 70, "[?] Ragu-Ragu", new Color(217, 119, 6));
        drawExamNavBtn(g, 570, 1715, 200, 70, "Lembar Jawab", new Color(79, 70, 229));
        drawExamNavBtn(g, 810, 1715, 180, 70, "Berikutnya >>", new Color(13, 71, 161));

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void drawExamOption(Graphics2D g, int x, int y, int w, int h, String key, String text, boolean selected) {
        RoundRectangle2D card = new RoundRectangle2D.Double(x, y, w, h, 20, 20);
        if (selected) {
            g.setColor(new Color(238, 242, 255));
            g.fill(card);
            g.setColor(new Color(79, 70, 229));
            g.setStroke(new BasicStroke(3f));
            g.draw(card);

            g.setColor(new Color(79, 70, 229));
            g.fillOval(x + 30, y + (h - 56) / 2, 56, 56);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString(key, x + 48, y + (h + 18) / 2);

            g.setColor(new Color(49, 46, 129));
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString(text, x + 110, y + (h + 18) / 2);
        } else {
            g.setColor(Color.WHITE);
            g.fill(card);
            g.setColor(new Color(226, 232, 240));
            g.setStroke(new BasicStroke(2f));
            g.draw(card);

            g.setColor(new Color(241, 245, 249));
            g.fillOval(x + 30, y + (h - 56) / 2, 56, 56);
            g.setColor(new Color(100, 116, 139));
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString(key, x + 48, y + (h + 18) / 2);

            g.setColor(new Color(30, 41, 59));
            g.setFont(new Font("Arial", Font.PLAIN, 28));
            g.drawString(text, x + 110, y + (h + 18) / 2);
        }
    }

    private static void drawExamNavBtn(Graphics2D g, int x, int y, int w, int h, String label, Color bg) {
        RoundRectangle2D btn = new RoundRectangle2D.Double(x, y, w, h, 16, 16);
        g.setColor(bg);
        g.fill(btn);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(label);
        g.drawString(label, x + (w - tw) / 2, y + (h + 16) / 2);
    }

    // ==========================================
    // 5. SCREENSHOT 3 - RESULT & ANALYTICS (1080x1920)
    // ==========================================
    private static void generateScreenshot3(File file) throws Exception {
        int width = 1080;
        int height = 1920;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Header Background
        GradientPaint hBg = new GradientPaint(0, 0, new Color(22, 101, 52), 0, 450, new Color(21, 128, 61));
        g.setPaint(hBg);
        g.fillRect(0, 0, width, 450);

        // Marketing Title
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.WHITE);
        g.drawString("Analisis Skor & Passing Grade", 80, 130);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.setColor(new Color(220, 252, 231));
        g.drawString("Evaluasi Akurat Berdasarkan Standar Resmi BKN", 80, 190);

        // Trophy / Passing Status Banner
        RoundRectangle2D trophyCard = new RoundRectangle2D.Double(80, 250, 920, 360, 32, 32);
        g.setColor(Color.WHITE);
        g.fill(trophyCard);
        g.setColor(new Color(226, 232, 240));
        g.draw(trophyCard);

        // Trophy Icon Circle
        g.setColor(new Color(254, 240, 138));
        g.fillOval(130, 290, 110, 110);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.setColor(new Color(161, 98, 7));
        g.drawString("TOP", 140, 365);

        g.setFont(new Font("Arial", Font.BOLD, 42));
        g.setColor(new Color(21, 128, 61));
        g.drawString("SELAMAT! ANDA LOLOS", 270, 335);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.setColor(new Color(71, 85, 105));
        g.drawString("Memenuhi Ambang Batas Nasional BKN SKD 2026", 270, 380);

        // Big Total Score Display
        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.setColor(new Color(15, 23, 42));
        g.drawString("428", 270, 500);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.setColor(new Color(100, 116, 139));
        g.drawString("/ 550 Poin Maksimal", 420, 495);

        // Body Canvas
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 640, width, height - 640);

        // Section breakdown title
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(30, 41, 59));
        g.drawString("Rincian Nilai Per Subtes", 80, 710);

        // Subtest Card 1: TWK
        drawSubtestResultCard(g, 80, 750, 920, 190,
                "Tes Wawasan Kebangsaan (TWK)",
                "Skor Anda: 95", "Ambang Batas: 65", "LOLOS",
                new Color(22, 101, 52), 0.63f
        );

        // Subtest Card 2: TIU
        drawSubtestResultCard(g, 80, 970, 920, 190,
                "Tes Intelegensia Umum (TIU)",
                "Skor Anda: 140", "Ambang Batas: 80", "LOLOS",
                new Color(22, 101, 52), 0.80f
        );

        // Subtest Card 3: TKP
        drawSubtestResultCard(g, 80, 1190, 920, 190,
                "Tes Karakteristik Pribadi (TKP)",
                "Skor Anda: 193", "Ambang Batas: 166", "LOLOS",
                new Color(22, 101, 52), 0.85f
        );

        // Performance Statistics (Speed & Accuracy)
        RoundRectangle2D statCard = new RoundRectangle2D.Double(80, 1420, 920, 180, 24, 24);
        g.setColor(Color.WHITE);
        g.fill(statCard);
        g.setColor(new Color(226, 232, 240));
        g.draw(statCard);

        drawStatBox(g, 100, 1450, 260, "Tingkat Akurasi", "84.5%", new Color(13, 71, 161));
        drawStatBox(g, 410, 1450, 260, "Kecepatan Rata-rata", "48 detik/soal", new Color(15, 118, 110));
        drawStatBox(g, 720, 1450, 260, "Waktu Selesai", "88:24 Menit", new Color(124, 45, 18));

        // Action Buttons
        drawExamNavBtn(g, 80, 1660, 440, 85, "Lihat Pembahasan", new Color(13, 71, 161));
        drawExamNavBtn(g, 560, 1660, 440, 85, "Coba Ujian Lagi (Acak)", new Color(79, 70, 229));

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void drawSubtestResultCard(Graphics2D g, int x, int y, int w, int h,
                                             String title, String score, String passing, String badge,
                                             Color color, float progress) {
        RoundRectangle2D card = new RoundRectangle2D.Double(x, y, w, h, 20, 20);
        g.setColor(Color.WHITE);
        g.fill(card);
        g.setColor(new Color(226, 232, 240));
        g.draw(card);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(new Color(15, 23, 42));
        g.drawString(title, x + 35, y + 55);

        // Passing badge
        RoundRectangle2D bPill = new RoundRectangle2D.Double(x + w - 160, y + 25, 125, 45, 22, 22);
        g.setColor(new Color(220, 252, 231));
        g.fill(bPill);
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(badge, x + w - 135, y + 55);

        // Score vs Passing text
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(color);
        g.drawString(score, x + 35, y + 105);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(new Color(100, 116, 139));
        g.drawString("- " + passing, x + 230, y + 105);

        // Progress Bar Background
        RoundRectangle2D pBg = new RoundRectangle2D.Double(x + 35, y + 130, w - 70, 20, 10, 10);
        g.setColor(new Color(241, 245, 249));
        g.fill(pBg);

        // Progress Bar Fill
        RoundRectangle2D pFill = new RoundRectangle2D.Double(x + 35, y + 130, (w - 70) * progress, 20, 10, 10);
        g.setColor(color);
        g.fill(pFill);
    }

    private static void drawStatBox(Graphics2D g, int x, int y, int w, String label, String val, Color c) {
        g.setFont(new Font("Arial", Font.PLAIN, 22));
        g.setColor(new Color(100, 116, 139));
        g.drawString(label, x, y + 30);

        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(c);
        g.drawString(val, x, y + 80);
    }

    // ==========================================
    // 6. SCREENSHOT 4 - REVIEW & KATEX FORMULA (1080x1920)
    // ==========================================
    private static void generateScreenshot4(File file) throws Exception {
        int width = 1080;
        int height = 1920;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // Header Background
        GradientPaint hBg = new GradientPaint(0, 0, new Color(30, 41, 59), 0, 300, new Color(51, 65, 85));
        g.setPaint(hBg);
        g.fillRect(0, 0, width, 300);

        // Marketing Headline
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.WHITE);
        g.drawString("Pembahasan Tuntas & Rumus Sains", 80, 120);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.setColor(new Color(203, 213, 225));
        g.drawString("Dilengkapi Dukungan Rendering Rumus Matematika KaTeX", 80, 175);

        // Filter chips bar
        drawReviewFilterChip(g, 80, 220, "Semua (110)", false);
        drawReviewFilterChip(g, 300, 220, "Salah (14)", false);
        drawReviewFilterChip(g, 500, 220, "Benar (96)", true);
        drawReviewFilterChip(g, 720, 220, "Disimpan (Bookmark)", false);

        // Body Canvas
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 300, width, height - 300);

        // Review Question Card
        RoundRectangle2D qCard = new RoundRectangle2D.Double(80, 340, 920, 520, 28, 28);
        g.setColor(Color.WHITE);
        g.fill(qCard);
        g.setColor(new Color(226, 232, 240));
        g.draw(qCard);

        // Bookmark Icon & Header
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(new Color(13, 71, 161));
        g.drawString("Soal No. 42 (Penalaran Matematika UTBK)", 120, 410);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.setColor(new Color(15, 23, 42));
        g.drawString("Tentukan himpunan penyelesaian dari persamaan kuadrat:", 120, 480);

        // Render Formula box (KaTeX simulation)
        RoundRectangle2D mathBox = new RoundRectangle2D.Double(120, 520, 840, 110, 18, 18);
        g.setColor(new Color(241, 245, 249));
        g.fill(mathBox);
        g.setColor(new Color(203, 213, 225));
        g.draw(mathBox);

        g.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 38));
        g.setColor(new Color(30, 41, 59));
        g.drawString("f(x) = 2x^2 - 8x + 6 = 0  =>  x1,2 = ?", 160, 590);

        // Options comparison
        drawReviewOption(g, 120, 660, 840, 75, "Kunci Jawaban Resmi: x = 1 atau x = 3", true);
        drawReviewOption(g, 120, 755, 840, 75, "Jawaban Anda: x = 1 atau x = 3  (BENAR +4 Poin)", true);

        // Detailed Explanation Card
        RoundRectangle2D expCard = new RoundRectangle2D.Double(80, 900, 920, 800, 28, 28);
        g.setColor(Color.WHITE);
        g.fill(expCard);
        g.setColor(new Color(226, 232, 240));
        g.draw(expCard);

        // Explanation Header
        g.setFont(new Font("Arial", Font.BOLD, 34));
        g.setColor(new Color(15, 118, 110));
        g.drawString("Pembahasan & Trik Cepat", 120, 970);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.setColor(new Color(51, 65, 85));
        g.drawString("Langkah 1: Bagi kedua ruas persamaan dengan 2:", 120, 1030);

        RoundRectangle2D step1Box = new RoundRectangle2D.Double(120, 1060, 840, 75, 14, 14);
        g.setColor(new Color(240, 253, 250));
        g.fill(step1Box);
        g.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 32));
        g.setColor(new Color(13, 148, 136));
        g.drawString("x^2 - 4x + 3 = 0", 150, 1110);

        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.setColor(new Color(51, 65, 85));
        g.drawString("Langkah 2: Faktorkan bentuk kuadrat tersebut:", 120, 1180);

        RoundRectangle2D step2Box = new RoundRectangle2D.Double(120, 1210, 840, 75, 14, 14);
        g.setColor(new Color(240, 253, 250));
        g.fill(step2Box);
        g.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 32));
        g.setColor(new Color(13, 148, 136));
        g.drawString("(x - 1)(x - 3) = 0   =>   x = 1 atau x = 3", 150, 1260);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(new Color(30, 41, 59));
        g.drawString("TRIK CEPAT CARA CERDAS:", 120, 1340);
        g.setFont(new Font("Arial", Font.PLAIN, 26));
        g.setColor(new Color(71, 85, 105));
        g.drawString("Ingat rumus jumlah dan hasil kali akar:", 120, 1385);
        g.drawString("- x1 + x2 = -b/a = -(-8)/2 = 4", 140, 1430);
        g.drawString("- x1 * x2 = c/a = 6/2 = 3", 140, 1475);
        g.drawString("Dua bilangan yang dijumlahkan = 4 dan dikalikan = 3 adalah 1 dan 3.", 120, 1530);

        // Bookmark action chip at bottom of card
        RoundRectangle2D bmBtn = new RoundRectangle2D.Double(120, 1590, 360, 70, 16, 16);
        g.setColor(new Color(254, 240, 138));
        g.fill(bmBtn);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(new Color(133, 77, 14));
        g.drawString("[*] Tersimpan di Bookmark", 150, 1634);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void drawReviewFilterChip(Graphics2D g, int x, int y, String label, boolean active) {
        RoundRectangle2D chip = new RoundRectangle2D.Double(x, y, 190, 55, 28, 28);
        if (active) {
            g.setColor(Color.WHITE);
            g.fill(chip);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(new Color(30, 41, 59));
            g.drawString(label, x + 25, y + 36);
        } else {
            g.setColor(new Color(255, 255, 255, 30));
            g.fill(chip);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            g.setColor(Color.WHITE);
            g.drawString(label, x + 25, y + 36);
        }
    }

    private static void drawReviewOption(Graphics2D g, int x, int y, int w, int h, String text, boolean isCorrect) {
        RoundRectangle2D opt = new RoundRectangle2D.Double(x, y, w, h, 14, 14);
        g.setColor(new Color(240, 253, 244));
        g.fill(opt);
        g.setColor(new Color(34, 197, 94));
        g.setStroke(new BasicStroke(2f));
        g.draw(opt);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(new Color(22, 101, 52));
        g.drawString("[V]  " + text, x + 25, y + 46);
    }
}

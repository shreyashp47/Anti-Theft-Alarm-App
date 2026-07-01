import os
from PIL import Image, ImageDraw, ImageFont

OUT_DIR = "store_assets"
os.makedirs(f"{OUT_DIR}/screenshots", exist_ok=True)

# New dark theme palette
GUARD = (15, 110, 86)        # #0F6E56 deep teal
MINT = (93, 202, 165)        # #5DCAA5
ALARM = (226, 75, 74)         # #E24B4A coral
BG = (18, 21, 26)             # #12151A near-black
SURFACE = (27, 32, 39)        # #1B2027 charcoal
TEXT = (242, 241, 236)        # #F2F1EC off-white
TEXT_SEC = (139, 138, 130)    # #8B8A82 warm gray
BORDER = (42, 47, 54)         # #2A2F36
SHIELD_LIGHT = (225, 245, 238) # #E1F5EE mint light
WHITE = (255, 255, 255)

def draw_rounded_rect(draw, xy, radius, fill):
    draw.rounded_rectangle(xy, radius=radius, fill=fill)

def cubic_bezier(p0, p1, p2, p3, num_points=30):
    """Approximate a cubic bezier curve with line segments"""
    points = []
    for i in range(num_points + 1):
        t = i / num_points
        mt = 1 - t
        x = mt**3 * p0[0] + 3 * mt**2 * t * p1[0] + 3 * mt * t**2 * p2[0] + t**3 * p3[0]
        y = mt**3 * p0[1] + 3 * mt**2 * t * p1[1] + 3 * mt * t**2 * p2[1] + t**3 * p3[1]
        points.append((x, y))
    return points

def draw_shield(draw, cx, cy, size, fill_color, accent_color):
    """Draw the shield-with-eye icon matching the SVG design"""
    s = size / 64
    offset_x = cx - 32 * s
    offset_y = cy - 32 * s

    def p(x, y):
        return (offset_x + x * s, offset_y + y * s)

    # Build shield path
    # Start at (32, 6) - top center notch
    pts = [p(32, 6)]
    # Line to (54, 14) - top right
    pts.append(p(54, 14))
    # Line to (54, 30) - right side
    pts.append(p(54, 30))
    # Curve to bottom (32, 58)
    pts.extend(cubic_bezier(p(54, 30), p(54, 44), p(44, 54), p(32, 58))[1:])
    # Curve to left side (10, 30)
    pts.extend(cubic_bezier(p(32, 58), p(20, 54), p(10, 44), p(10, 30))[1:])
    # Line to (10, 14)
    pts.append(p(10, 14))
    # Close back to (32, 6)
    pts.append(p(32, 6))

    draw.polygon(pts, fill=fill_color)

    # Eye marks (two small curved lines above the eye)
    # SVG: M27 21 C27 25 27 25 24 27
    marks = cubic_bezier(p(27, 21), p(27, 25), p(27, 25), p(24, 27), 8)
    marks2 = cubic_bezier(p(37, 21), p(37, 25), p(37, 25), p(40, 27), 8)
    draw.line(marks, fill=fill_color, width=max(2, int(s * 2.5)))
    draw.line(marks2, fill=fill_color, width=max(2, int(s * 2.5)))

    # Eye ellipse (teal)
    eye_bbox = [p(20, 22), p(44, 38)]
    draw.ellipse([eye_bbox[0][0], eye_bbox[0][1], eye_bbox[1][0], eye_bbox[1][1]],
                 fill=accent_color)

    # Pupil (mint circle)
    pupil_c = p(32, 30)
    pupil_r = 4 * s
    draw.ellipse([pupil_c[0] - pupil_r, pupil_c[1] - pupil_r,
                  pupil_c[0] + pupil_r, pupil_c[1] + pupil_r],
                 fill=fill_color)

    # Arrow below eye
    # SVG: M32 39 L29 44 H35 Z
    arrow = [p(32, 39), p(29, 44), p(35, 44)]
    draw.polygon(arrow, fill=accent_color)


def create_icon():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Rounded square background
    draw_rounded_rect(draw, (0, 0, 512, 512), 112, GUARD)

    # Shield icon at center, size 300
    draw_shield(draw, 256, 256, 300, SHIELD_LIGHT, GUARD)

    img.save(f"{OUT_DIR}/icon-512.png")
    print("✓ icon-512.png")


def create_feature_graphic():
    img = Image.new("RGB", (1024, 500), BG)
    draw = ImageDraw.Draw(img)

    # Accent stripe at top
    draw.rectangle([0, 0, 1024, 6], fill=GUARD)

    # Card in center
    draw_rounded_rect(draw, (50, 120, 974, 380), 40, SURFACE)
    draw.rounded_rectangle([50, 120, 974, 380], radius=40, outline=BORDER, width=2)

    # Small shield icon on card
    draw_shield(draw, 512, 195, 56, SHIELD_LIGHT, GUARD)

    try:
        font_title = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 44)
        font_sub = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 24)
    except Exception:
        font_title = ImageFont.load_default()
        font_sub = font_title

    draw.text((512, 240), "AntiTheft Alarm", fill=TEXT, font=font_title, anchor="mt")
    draw.text((512, 295), "Intelligent theft detection for your Android device",
              fill=TEXT_SEC, font=font_sub, anchor="mt")

    img.save(f"{OUT_DIR}/feature-graphic.png")
    print("✓ feature-graphic.png")


def create_screenshot_pin_create():
    img = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(img)

    # Status bar
    draw.rectangle([0, 0, 1080, 80], fill=BG)

    # Lock icon
    draw_shield(draw, 540, 240, 100, SHIELD_LIGHT, GUARD)

    # Title
    try:
        font_title = ImageFont.truetype("/System/Library/AssetsV2/Compatible/Mac/SystemFonts/Montserrat.otf", 44) if os.path.exists("/System/Library/AssetsV2/Compatible/Mac/SystemFonts/Montserrat.otf") else ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 44)
        font_body = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 28)
        font_key = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 36)
    except:
        font_title = ImageFont.load_default()
        font_body = font_title
        font_key = font_title

    draw.text((540, 340), "Create a PIN", fill=TEXT, font=font_title, anchor="mt")
    draw.text((540, 400), "Choose a 4-digit PIN to secure your app",
              fill=TEXT_SEC, font=font_body, anchor="mt")

    # PIN dots (2 filled, 2 empty)
    for i in range(4):
        cx = 390 + i * 100
        r = 12
        color = GUARD if i < 2 else SURFACE
        draw.ellipse([cx - r, 470 - r, cx + r, 470 + r], fill=color)

    # Keypad
    keys = [["1", "2", "3"], ["4", "5", "6"], ["7", "8", "9"]]
    key_size = 90
    gap = 24
    start_y = 560
    for row_idx, row in enumerate(keys):
        y = start_y + row_idx * (key_size + gap)
        total_width = 3 * key_size + 2 * gap
        start_x = (1080 - total_width) // 2
        for col_idx, key in enumerate(row):
            x = start_x + col_idx * (key_size + gap)
            draw_rounded_rect(draw, (x, y, x + key_size, y + key_size), 20, SURFACE)
            draw.text((x + key_size // 2, y + key_size // 2), key,
                      fill=TEXT, font=font_key, anchor="mm")

    # Bottom row: spacer, 0, backspace
    y = start_y + 3 * (key_size + gap)
    total_width = 2 * key_size + gap
    start_x = (1080 - total_width) // 2
    x = start_x
    draw_rounded_rect(draw, (x, y, x + key_size, y + key_size), 20, SURFACE)
    draw.text((x + key_size // 2, y + key_size // 2), "0", fill=TEXT, font=font_key, anchor="mm")

    # Navigation bar
    draw.rectangle([0, 1830, 1080, 1920], fill=BG)
    draw.line([360, 1870, 720, 1870], fill=TEXT_SEC, width=4)

    img.save(f"{OUT_DIR}/screenshots/01-pin-create.png")
    print("✓ screenshots/01-pin-create.png")


def create_screenshot_pin_entry():
    img = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, 1080, 80], fill=BG)

    # Lock icon
    draw_shield(draw, 540, 240, 100, SHIELD_LIGHT, GUARD)

    try:
        font_title = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 44)
        font_body = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 28)
        font_key = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 36)
    except:
        font_title = ImageFont.load_default()
        font_body = font_title
        font_key = font_title

    draw.text((540, 340), "Enter PIN", fill=TEXT, font=font_title, anchor="mt")
    draw.text((540, 400), "Enter your 4-digit PIN to continue",
              fill=TEXT_SEC, font=font_body, anchor="mt")

    # PIN dots - one filled with GUARD
    for i in range(4):
        cx = 390 + i * 100
        r = 12
        color = GUARD if i < 1 else SURFACE
        draw.ellipse([cx - r, 470 - r, cx + r, 470 + r], fill=color)

    # Keypad (same as create)
    keys = [["1", "2", "3"], ["4", "5", "6"], ["7", "8", "9"]]
    key_size = 90
    gap = 24
    start_y = 560
    for row_idx, row in enumerate(keys):
        y = start_y + row_idx * (key_size + gap)
        total_width = 3 * key_size + 2 * gap
        start_x = (1080 - total_width) // 2
        for col_idx, key in enumerate(row):
            x = start_x + col_idx * (key_size + gap)
            draw_rounded_rect(draw, (x, y, x + key_size, y + key_size), 20, SURFACE)
            draw.text((x + key_size // 2, y + key_size // 2), key,
                      fill=TEXT, font=font_key, anchor="mm")

    y = start_y + 3 * (key_size + gap)
    total_width = 2 * key_size + gap
    start_x = (1080 - total_width) // 2
    draw_rounded_rect(draw, (start_x, y, start_x + key_size, y + key_size), 20, SURFACE)
    draw.text((start_x + key_size // 2, y + key_size // 2), "0", fill=TEXT, font=font_key, anchor="mm")

    draw.rectangle([0, 1830, 1080, 1920], fill=BG)
    draw.line([360, 1870, 720, 1870], fill=TEXT_SEC, width=4)

    img.save(f"{OUT_DIR}/screenshots/02-pin-entry.png")
    print("✓ screenshots/02-pin-entry.png")


def create_screenshot_home_armed():
    img = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, 1080, 80], fill=BG)

    try:
        font_title = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 44)
        font_body = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 28)
        font_small = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 24)
    except:
        font_title = ImageFont.load_default()
        font_body = font_title
        font_small = font_title

    # Guard mode card
    draw_rounded_rect(draw, (80, 140, 1000, 340), 28, SURFACE)
    draw.rounded_rectangle([80, 140, 1000, 340], radius=28, outline=BORDER, width=2)

    draw.text((540, 190), "Armed \u00b7 all clear", fill=MINT, font=font_body, anchor="mt")

    # Toggle
    toggle_x, toggle_y = 540, 250
    draw_rounded_rect(draw, (toggle_x - 40, toggle_y - 12, toggle_x + 40, toggle_y + 12), 14, GUARD)
    draw.ellipse([toggle_x + 22, toggle_y - 10, toggle_x + 38, toggle_y + 10], fill=WHITE)

    draw.text((540, 290), "Guard mode", fill=TEXT_SEC, font=font_small, anchor="mt")

    # Feature cards
    features = [
        ("Charging guard", GUARD, True),
        ("Motion guard", GUARD, True),
        ("SIM guard", SURFACE, False),
    ]
    for i, (label, color, active) in enumerate(features):
        y = 380 + i * 90
        draw_rounded_rect(draw, (80, y, 1000, y + 76), 18, SURFACE)
        draw.text((120, y + 38), label, fill=TEXT, font=font_body, anchor="lm")
        if active:
            toggle_cx = 930
            draw_rounded_rect(draw, (toggle_cx - 30, y + 28, toggle_cx + 30, y + 48), 12, GUARD)
            draw.ellipse([toggle_cx + 14, y + 30, toggle_cx + 28, y + 46], fill=WHITE)
        else:
            toggle_cx = 930
            draw_rounded_rect(draw, (toggle_cx - 30, y + 28, toggle_cx + 30, y + 48), 12, SURFACE)
            draw.rounded_rectangle([toggle_cx - 30, y + 28, toggle_cx + 30, y + 48], radius=12, outline=BORDER, width=2)
            draw.ellipse([toggle_cx - 26, y + 30, toggle_cx - 12, y + 46], fill=TEXT_SEC)

    draw.rectangle([0, 1830, 1080, 1920], fill=BG)
    draw.line([360, 1870, 720, 1870], fill=TEXT_SEC, width=4)

    img.save(f"{OUT_DIR}/screenshots/03-home-armed.png")
    print("✓ screenshots/03-home-armed.png")


if __name__ == "__main__":
    create_icon()
    create_feature_graphic()
    create_screenshot_pin_create()
    create_screenshot_pin_entry()
    create_screenshot_home_armed()
    print("\nAll assets generated in 'store_assets/'")

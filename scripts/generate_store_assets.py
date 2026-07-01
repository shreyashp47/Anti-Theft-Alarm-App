import os
from PIL import Image, ImageDraw, ImageFont

OUT_DIR = "store_assets"
os.makedirs(f"{OUT_DIR}/screenshots", exist_ok=True)

GREEN = (27, 94, 32)
DARK_GREEN = (0, 51, 0)
ORANGE = (255, 111, 0)
WHITE = (255, 255, 255)
DARK = (33, 33, 33)
GRAY = (158, 158, 158)
BG = (245, 245, 245)
RED = (211, 47, 47)

def draw_rounded_rect(draw, xy, radius, fill):
    x1, y1, x2, y2 = xy
    draw.rounded_rectangle(xy, radius=radius, fill=fill)

def create_icon():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    circle = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    cdraw = ImageDraw.Draw(circle)
    cdraw.ellipse([56, 56, 456, 456], fill=GREEN)
    img.paste(circle, (0, 0), circle)

    cdraw.ellipse([168, 168, 344, 344], fill=WHITE)

    draw.polygon([(220, 200), (220, 256), (164, 256), (164, 312),
                   (220, 312), (220, 368), (292, 368), (292, 312),
                   (348, 312), (348, 256), (292, 256), (292, 200)],
                  fill=WHITE)

    img.save(f"{OUT_DIR}/icon-512.png")
    print("✓ icon-512.png")

def create_feature_graphic():
    img = Image.new("RGB", (1024, 500), GREEN)
    draw = ImageDraw.Draw(img)

    draw.rounded_rectangle([50, 150, 974, 350], radius=40, fill=DARK_GREEN)

    try:
        font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 48)
        font_small = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 28)
    except:
        font = ImageFont.load_default()
        font_small = font

    draw.text((512, 190), "AntiTheft Alarm", fill=WHITE, font=font, anchor="mt")
    draw.text((512, 260), "Intelligent theft detection for your Android device",
              fill=(200, 230, 200), font=font_small, anchor="mt")

    draw.rectangle([0, 0, 1024, 8], fill=ORANGE)
    img.save(f"{OUT_DIR}/feature-graphic.png")
    print("✓ feature-graphic.png")

def create_screenshot_pin_create():
    img = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, 1080, 160], fill=GREEN)
    draw.text((540, 80), "AntiTheft Alarm", fill=WHITE, font=font_lg(), anchor="mt")

    draw.text((540, 350), "Set Your PIN", fill=DARK, font=font_lg(), anchor="mt")

    draw_rounded_rect(draw, (140, 480, 940, 620), 16, WHITE)
    draw.rectangle([140, 480, 940, 620], outline=(200, 200, 200), width=2)
    draw.text((160, 510), "Enter a 4-digit PIN", fill=GRAY, font=font_sm(), anchor="lt")
    draw.line([160, 590, 920, 590], fill=(200, 200, 200), width=2)

    draw_rounded_rect(draw, (140, 680, 940, 820), 16, WHITE)
    draw.rectangle([140, 680, 940, 820], outline=(200, 200, 200), width=2)
    draw.text((160, 710), "Confirm PIN", fill=GRAY, font=font_sm(), anchor="lt")
    draw.line([160, 790, 920, 790], fill=(200, 200, 200), width=2)

    draw_rounded_rect(draw, (140, 880, 940, 1000), 60, GREEN)
    draw.text((540, 940), "Set PIN", fill=WHITE, font=font_md(), anchor="mt")

    draw_rounded_rect(draw, (0, 1830, 1080, 1920), 0, WHITE)
    draw.line([360, 1870, 720, 1870], fill=DARK, width=4)

    img.save(f"{OUT_DIR}/screenshots/01-pin-create.png")
    print("✓ screenshots/01-pin-create.png")

def create_screenshot_pin_entry():
    img = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, 1080, 160], fill=GREEN)
    draw.text((540, 80), "AntiTheft Alarm", fill=WHITE, font=font_lg(), anchor="mt")

    draw.text((540, 350), "Enter PIN", fill=DARK, font=font_lg(), anchor="mt")

    draw_rounded_rect(draw, (140, 480, 940, 620), 16, WHITE)
    draw.rectangle([140, 480, 940, 620], outline=(200, 200, 200), width=2)
    draw.text((160, 510), "Enter your PIN", fill=GRAY, font=font_sm(), anchor="lt")
    for i in range(4):
        cx = 320 + i * 120
        draw.ellipse([cx - 20, 565, cx + 20, 605], fill=DARK)

    draw_rounded_rect(draw, (140, 880, 940, 1000), 60, GREEN)
    draw.text((540, 940), "Unlock", fill=WHITE, font=font_md(), anchor="mt")

    draw_rounded_rect(draw, (0, 1830, 1080, 1920), 0, WHITE)
    draw.line([360, 1870, 720, 1870], fill=DARK, width=4)

    img.save(f"{OUT_DIR}/screenshots/02-pin-entry.png")
    print("✓ screenshots/02-pin-entry.png")

def create_screenshot_home_armed():
    img = Image.new("RGB", (1080, 1920), BG)
    draw = ImageDraw.Draw(img)

    draw.rectangle([0, 0, 1080, 160], fill=GREEN)
    draw.text((540, 80), "AntiTheft Alarm", fill=WHITE, font=font_lg(), anchor="mt")

    draw.text((540, 450), "AntiTheft Alarm", fill=DARK, font=font_lg(), anchor="mt")
    draw.text((540, 530), "Your device is protected.", fill=GRAY, font=font_md(), anchor="mt")

    draw_rounded_rect(draw, (390, 700, 690, 780), 40, RED)
    draw.text((540, 740), "ARMED", fill=WHITE, font=font_md(), anchor="mt")

    draw.rectangle([440, 730, 640, 735], fill=(150, 30, 30))

    draw_rounded_rect(draw, (0, 1830, 1080, 1920), 0, WHITE)
    draw.line([360, 1870, 720, 1870], fill=DARK, width=4)

    img.save(f"{OUT_DIR}/screenshots/03-home-armed.png")
    print("✓ screenshots/03-home-armed.png")

def font_lg():
    try:
        return ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 56)
    except:
        return ImageFont.load_default()

def font_md():
    try:
        return ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 40)
    except:
        return ImageFont.load_default()

def font_sm():
    try:
        return ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 32)
    except:
        return ImageFont.load_default()

if __name__ == "__main__":
    create_icon()
    create_feature_graphic()
    create_screenshot_pin_create()
    create_screenshot_pin_entry()
    create_screenshot_home_armed()
    print("\nAll assets generated in 'store_assets/'")

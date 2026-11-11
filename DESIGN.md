# PayDz — Design System & Visual Replication Specification (`1000648354.png`)

## 1. Visual Analysis of Reference Image (`1000648354.png`)
- **Theme Polarity:** Strictly High-Craft Light Mode (`#F2F6FC` porcelain background, `#FFFFFF` elevated cards). Dark mode and black backgrounds are strictly disabled.
- **Brand Palette:**
  - **Primary Royal Blue:** `#0057D9` (Buttons, active filter pills, icons, headers)
  - **Deep Royal Blue:** `#003EA8` (Splash top gradient, Home curved header top)
  - **Cyan-Blue Highlight:** `#0E87F2` $\rightarrow$ `#22B8F0` (Balance card horizontal gradient, Virtual PayDz card highlight)
  - **Accent Gold:** `#FFB800` (Speed lines in PayDz logo, notification bell, coins icon, lightning bolt)
  - **Semantic Status:** `#16A34A` / `#22C55E` (Incoming transactions `+`, success checkmark) and `#EF4444` (Outgoing transactions `-`)
  - **Typography Colors:** `#071C4E` (Deep navy headings & titles) and `#5E7196` (Slate subtitles & placeholders)

## 2. Typography & RTL Rules
- **Arabic Font Family:** `Cairo` (`res/font/cairo.ttf`) for all Arabic headings, labels, and action buttons.
- **Tabular Numerals & Card Masks:** `Roboto Mono` (`res/font/roboto_mono.ttf`) with `Locale.US` formatting so amounts (`125,750.00 دج`, `+10,000.00 دج`, `-5,000.00 دج`), dates (`07/11`), phone numbers (`06 12 34 56 78`), and card masks (`**** 4567`, `**** 8421`) never invert digit order in RTL mode.

## 3. Component Tokens & Geometry
- **Buttons & Inputs:** Full pill radius (`RoundedCornerShape(50)`), `56.dp` height, subtle `1.dp` border (`#DCE5F5`), soft elevation (`2.dp` for inputs, `6.dp` for primary blue buttons).
- **Service & Action Cards:** Squircle radius (`22.dp`–`26.dp`), pure white surface (`#FFFFFF`), `1.dp` soft border (`#E5EEF9`), `3.dp`–`4.dp` elevation.
- **Bottom Navigation Bar:** 4 items (`الرئيسية`, `المعاملات`, `الخدمات`, `المزيد`) inside a white surface with `24.dp` top corner radius.

---
name: Fuel Density Converter
colors:
  surface: '#f6f9fe'
  surface-dim: '#d7dadf'
  surface-bright: '#f6f9fe'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f4f9'
  surface-container: '#ebeef3'
  surface-container-high: '#e5e8ed'
  surface-container-highest: '#dfe3e8'
  on-surface: '#181c20'
  on-surface-variant: '#45474d'
  inverse-surface: '#2d3135'
  inverse-on-surface: '#eef1f6'
  outline: '#75777e'
  outline-variant: '#c5c6ce'
  surface-tint: '#525e7b'
  primary: '#000310'
  on-primary: '#ffffff'
  primary-container: '#101d36'
  on-primary-container: '#7985a3'
  inverse-primary: '#bac6e7'
  secondary: '#3755c3'
  on-secondary: '#ffffff'
  secondary-container: '#708cfd'
  on-secondary-container: '#00217a'
  tertiary: '#060300'
  on-tertiary: '#ffffff'
  tertiary-container: '#271c00'
  on-tertiary-container: '#a68000'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#bac6e7'
  on-primary-fixed: '#0e1b34'
  on-primary-fixed-variant: '#3a4762'
  secondary-fixed: '#dde1ff'
  secondary-fixed-dim: '#b8c4ff'
  on-secondary-fixed: '#001453'
  on-secondary-fixed-variant: '#173bab'
  tertiary-fixed: '#ffdf95'
  tertiary-fixed-dim: '#f4bf1b'
  on-tertiary-fixed: '#251a00'
  on-tertiary-fixed-variant: '#594400'
  background: '#f6f9fe'
  on-background: '#181c20'
  surface-variant: '#dfe3e8'
typography:
  display-lg:
    fontFamily: Space Grotesk
    fontSize: 44px
    fontWeight: '700'
    lineHeight: 52px
  display-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
  data-display:
    fontFamily: JetBrains Mono
    fontSize: 38px
    fontWeight: '700'
    lineHeight: 44px
  data-metric:
    fontFamily: JetBrains Mono
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  data-badge:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  gutter-desktop: 2rem
  margin: 1rem
  margin-tablet: 2rem
  margin-desktop: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system is engineered for industrial field operators, forensic auditors, and petrol pump managers who demand absolute precision under direct sunlight and high-pressure operational environments. The identity converges utilitarian Android Material You architecture with high-integrity financial instrumentation. It balances industrial ruggedness with institutional polish.

### Design Movement
- **Precision Modernism & Tactile Utility**: A synthesis of crisp surface architecture, ultra-legible tabular telemetry, and tactile, high-affordance control elements.
- **Instrumental Rigor**: Interface modules mimic calibrated analytical laboratory instruments while strictly adhering to fluid Android navigation ergonomics (thumb-zone mechanics, bottom sheets, and immediate haptic feedback metaphors).
- **High-Acuity Legibility**: Clean white cards sit over a cool tinted substrate, anchored by deep nautical authority tones and energized with high-visibility amber accents to prevent misread calculations under harsh forecourt glare.

## Colors

The palette establishes an immediate sense of institutional authority and high-precision measurement.

### Functional Roles & Semantic Mapping
- **Primary (`#101D36`)**: Anchor color for dominant structural elements, top app bars, primary data readouts, and core operational chrome.
- **Secondary (`#1E40AF`)**: Active selection states, field validation triggers, primary icon highlights, and interactive toggle surfaces.
- **Tertiary / Accent (`#FFC928`)**: High-priority call-to-actions, calculated result highlights, conversion triggers, and operational alert boundaries.
- **Background (`#F4F7FC`)**: Cool anti-glare canvas that limits eye fatigue while maintaining separation from foreground cards.
- **Surface / Card (`#FFFFFF`)**: Pure elevated planes reserved for editable forms, dial outputs, and analytical cards.
- **Surface Container High (`#E8EEF8`)**: Recessed input wells, inactive segmented toggles, and metadata chip backdrops.
- **Semantic Feedback**:
  - Success (`#16A34A`): ASTM 53B compliance passed, standard density match, tank dip validated.
  - Error (`#DC2626`): Out-of-spec density variances, invalid hydrometer readings, temperature threshold breaches.
  - Text Primary (`#0F172A`): Maximum optical contrast for primary metrics.
  - Text Secondary (`#64748B`): Descriptive labels, standard units, timestamps.
  - Text Inverted (`#FFFFFF`): High-contrast text on navy and royal blue containers.

## Typography

The typographic hierarchy implements a tri-font pairing to ensure maximum structural clarity between application framing, standard instructional reading, and scientific metrics.

- **Headlines & Titles (`Space Grotesk`)**: Technical, modern geometric letterforms that instill authoritative hardware-software cohesion.
- **Interface & Operational Copy (`Inter`)**: Neutral, highly legible humanist-grotesque ensuring optical comfort across complex forms, warnings, and modal dialogues.
- **Telemetry & Density Outputs (`JetBrains Mono`)**: Strictly applied to numeric computation panels, ASTM 53B table codes, standard density conversions (e.g., `832.4 kg/m³`), hydrometer offsets, and temperature readouts (`28.5 °C`). Always render numbers with tabular lining features (`tnum`) enabled to prevent layout shifting during real-time digit calculations.

## Layout & Spacing

Layout follows an 8dp baseline grid optimized for handheld Android hardware, thumb-centric operation, and ruggedized industrial tablets.

### Responsive Breakpoints & Columns
- **Compact (Mobile: &lt; 600dp)**: 4-column layout with `16px` gutters and `16px` outer margins. Primary action zones are anchored within the lower 40% vertical screen height.
- **Medium (Foldables / Tablets: 600dp - 840dp)**: 8-column layout with `24px` gutters and `32px` margins. Dual-pane layout separating input parameters (temperature, hydrometer reading) on the left from dynamic ASTM 53B conversion charts on the right.
- **Expanded (Field Desktops / Wall Consoles: &gt; 840dp)**: 12-column layout with `32px` gutters and fixed max-width container (`1200px`) centered with dynamic margins.

### Spacing Strategy
- **Padding (`space-md`, `space-lg`)**: Internal card padding uses generous spacing to safeguard tap accuracy when operators wear industrial gloves.
- **Touch Targets**: All interactive elements (keypad buttons, fuel toggles, dropdowns) enforce a mandatory minimum target size of `48x48dp`.

## Elevation & Depth

Visual hierarchy rejects harsh drop shadows in favor of ambient multi-layer light deflection and tonal stepping.

### Surface Tiers
- **Level 0 (Base)**: `#F4F7FC` background canvas. Flat, neutral ground plane.
- **Level 1 (Structural Cards)**: Pure white `#FFFFFF` with low-contrast structural perimeter: `border: 1px solid rgba(16, 29, 54, 0.06)` paired with diffused ambient shadow: `box-shadow: 0 4px 20px -2px rgba(16, 29, 54, 0.05)`.
- **Level 2 (Active Calculation Modules & Modals)**: Pure white `#FFFFFF` with `box-shadow: 0 12px 32px -4px rgba(16, 29, 54, 0.08), 0 4px 12px -2px rgba(16, 29, 54, 0.04)`.
- **Level 3 (Floating System Chrome & Navigation)**: Floating pill bar utilizing subtle frosted translucency (`rgba(255, 255, 255, 0.92)` backdrop filter `blur(16px)`) with boundary glow: `border: 1px solid rgba(255, 255, 255, 0.6)` and `box-shadow: 0 16px 36px -6px rgba(16, 29, 54, 0.12)`.

## Shapes

The design system implements a refined modern curvature hierarchy:
- **Base Components (`rounded`)**: 8px (`0.5rem`) for text fields, data badges, verification chips, and inline segmented controls.
- **Primary Cards (`rounded-lg`)**: 16px (`1rem`) for standard telemetry modules, summary readouts, and parameter entry cards.
- **Hero Containers & Action Surfaces (`rounded-xl`)**: 24px (`1.5rem`) for calculation result heroes, fuel-type cards, and bottom sheet containers.
- **Android System Chrome & Floating Nav**: Fully pill-shaped (`9999px`) for quick-action FABs, navigation status pills, and system gesture handles.

## Components

### Buttons
- **Primary CTA ("Calculate 15°C Density")**: Gradient fill from `#FFC928` to `#F5B700`, with subtle inner top highlight (`1px inset rgba(255, 255, 255, 0.4)`), label rendered in `#101D36` bold. Elevation `box-shadow: 0 8px 24px -4px rgba(255, 201, 40, 0.4)`. Active state drops scale to `0.98` with haptic confirmation.
- **Secondary Buttons**: Outlined container using `#E8EEF8` background with `#1E40AF` text, transition on press to solid `#1E40AF` with inverted white text.

### Fuel Type Selector Cards
- Segmented multi-card matrix (Petrol MS, Diesel HSD, Premium Speed, Bio-Diesel).
- **Default State**: Pure white surface, `1px solid rgba(16, 29, 54, 0.08)`, secondary text label, fuel icon.
- **Active State**: Primary navy `#101D36` fill, inverted text, golden amber `#FFC928` product indicator tag, accompanied by a subtle `#1E40AF` optical edge aura.

### Density Telemetry & Input Fields
- **Parameter Inputs (Observed Temp & Density)**: Dual-layer layout featuring an uppercase `label-sm` title, large numeric tabular readout, and pinned unit indicator (`kg/m³`, `°C`) in `#64748B`.
- Integrated quick-stepper adjustment buttons (`-0.1` / `+0.1`) embedded into the right rail of the input container.

### ASTM Table 53B Verification Badges
- Scientific audit verification pills rendered in `JetBrains Mono` (`data-badge`).
- **Compliant**: Background `rgba(22, 163, 74, 0.12)`, text `#16A34A`, border `1px solid rgba(22, 163, 74, 0.3)`. Accompanied by a check-seal icon.
- **Out of Spec / Calibration Required**: Background `rgba(220, 38, 38, 0.12)`, text `#DC2626`, flashing icon indicator.

### Android System Chrome & Navigation
- **Top System Status Bar**: Crisp light layout (`battery`, `Wi-Fi`, `9:41`) rendered in `#0F172A`.
- **Bottom Navigation Dock**: Floating rounded pill anchored 16dp above the bottom gesture line. Contains quick icons for `Converter`, `ASTM Tables`, `Tank Logbook`, and `Calibration`. Active tab features a golden amber micro-dot illumination indicator.
- **Android Gesture Pill**: Centered `72x4dp` bar colored in `#64748B` with `30%` opacity.
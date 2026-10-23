# Design System Master File

> **LOGIC:** When building a specific page, first check `design-system/pages/[page-name].md`.
> If that file exists, its rules **override** this Master file.
> If not, strictly follow the rules below.

---

**Project:** Aether AI
**Generated:** 2026-10-22 15:48:00
**Category:** AI/Chatbot Platform
**Design Dials:** Variance 6/10 (Balanced / Modern) | Motion 5/10 (Standard) | Density 6/10 (Standard)

---

## Global Rules

### Color Palette

| Role | Hex | CSS Variable |
|------|-----|--------------|
| Primary | `#2358D6` | `--color-primary` |
| On Primary | `#FFFFFF` | `--color-on-primary` |
| Accent | `#08776A` | `--color-accent` |
| On Accent | `#FFFFFF` | `--color-on-accent` |
| Background | `#F4F6FA` | `--color-background` |
| Foreground | `#142033` | `--color-foreground` |
| Card | `#FFFFFF` | `--color-card` |
| Card Foreground | `#142033` | `--color-card-foreground` |
| Muted Surface | `#E9EDF4` | `--color-muted-surface` |
| Muted Foreground | `#5F6C80` | `--color-muted-foreground` |
| Border | `#DCE3ED` | `--color-border` |
| Destructive | `#B3261E` | `--color-destructive` |
| Ring | `#2358D6` | `--color-ring` |

**Color Notes:** Porcelain surfaces, blue-black ink and cobalt actions. Use teal only as a secondary accent.

### Dark Theme

| Role | Hex |
|------|-----|
| Background | `#10151D` |
| Sidebar | `#121923` |
| Panel | `#18212D` |
| Foreground | `#F2F5F8` |
| Muted Foreground | `#9DAABA` |
| Border | `#2A3746` |
| Primary | `#7DA3FF` |
| On Primary | `#09101D` |
| Accent | `#76CBB5` |

Dark mode uses blue-black depth rather than warm brown. Primary actions remain cobalt and never turn into the light foreground color.

### Typography

- **Heading Font:** Instrument Serif
- **Body Font:** Inter
- **CJK Display Fallback:** Noto Serif SC → Source Han Serif SC → Songti SC → STSong → SimSun
- **Mood:** editorial, quiet, warm, precise, human, considered
- **Google Fonts:** [Instrument Serif + Inter](https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Inter:wght@400;500;600;700&display=swap)

**CSS Import:**
```css
@import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Inter:wght@400;500;600;700&display=swap');
```

### Spacing Variables

*Density: 6/10 — Standard*

| Token | Value | Usage |
|-------|-------|-------|
| `--space-xs` | `4px` / `0.25rem` | Tight gaps |
| `--space-sm` | `8px` / `0.5rem` | Icon gaps, inline spacing |
| `--space-md` | `16px` / `1rem` | Standard padding |
| `--space-lg` | `24px` / `1.5rem` | Section padding |
| `--space-xl` | `32px` / `2rem` | Large gaps |
| `--space-2xl` | `48px` / `3rem` | Section margins |
| `--space-3xl` | `64px` / `4rem` | Hero padding |

### Shadow Depths

| Level | Value | Usage |
|-------|-------|-------|
| `--shadow-sm` | `0 1px 2px rgba(0,0,0,0.05)` | Subtle lift |
| `--shadow-md` | `0 8px 24px rgba(32,31,27,0.08)` | Popovers |
| `--shadow-lg` | `0 18px 44px rgba(32,31,27,0.14)` | Modals and drawers |

---

## Component Specs

### Buttons

```css
/* Primary Button */
.btn-primary {
  background: #142033;
  color: #F4F6FA;
  padding: 11px 18px;
  border-radius: 10px;
  font-weight: 600;
  transition: background-color 150ms cubic-bezier(0.23, 1, 0.32, 1),
              transform 150ms cubic-bezier(0.23, 1, 0.32, 1);
  cursor: pointer;
}

.btn-primary:hover {
  background: #2358D6;
}

.btn-primary:active {
  transform: scale(0.97);
}

/* Secondary Button */
.btn-secondary {
  background: transparent;
  color: #142033;
  border: 1px solid #DCE3ED;
  padding: 11px 18px;
  border-radius: 10px;
  font-weight: 600;
  transition: background-color 150ms ease, border-color 150ms ease;
  cursor: pointer;
}
```

### Cards

```css
.assistant-message {
  background: transparent;
  border: 0;
  box-shadow: none;
  padding-block: 28px;
}

/* AI responses are open editorial text. Only user input uses a compact bubble. */
```

### Inputs

```css
.input {
  padding: 12px 16px;
  border: 1px solid #DCE3ED;
  border-radius: 10px;
  font-size: 16px;
  transition: border-color 200ms ease;
}

.input:focus {
  border-color: #2358D6;
  outline: 2px solid color-mix(in srgb, #2358D6 28%, transparent);
  outline-offset: 2px;
}
```

### Modals

```css
.modal-overlay {
  background: rgba(0, 0, 0, 0.5);
}

.modal {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 32px;
  box-shadow: var(--shadow-xl);
  max-width: 500px;
  width: 90%;
}
```

---

## Style Guidelines

**Style:** AI-Native UI

**Keywords:** Chatbot, conversational, voice, assistant, agentic, ambient, minimal chrome, streaming text, AI interactions

**Best For:** AI products, chatbots, voice assistants, copilots, AI-powered tools, conversational interfaces

**Key Effects:** Quiet streaming indicator, open editorial responses, restrained state transitions.

**Surface Treatment:** Use a fine dot field, one broad concentric ring and low-contrast cobalt/teal glows to add atmosphere without introducing heavy cards. Keep the main reading column calm and borderless.

### Page Pattern

**Pattern Name:** Product Demo + Features

- **Conversion Strategy:** Use an interactive demo only when it explains value better than static media. Provide captions, transcript, visible play/pause controls, and a non-video fallback; do not autoplay under reduced motion. Pause media when offscreen or hidden and keep the final product state available as static content.
- **CTA Placement:** Video center + CTA right/bottom
- **Section Order:** Hero > Product video/mockup (center) > Feature breakdown per section > Comparison (optional) > CTA

---

## Motion

**Purposeful UI motion** — Default duration 150-220ms with strong ease-out.

```css
:root {
  --ease-out: cubic-bezier(0.23, 1, 0.32, 1);
}

.interactive {
  transition: color 150ms var(--ease-out),
              background-color 150ms var(--ease-out),
              transform 150ms var(--ease-out);
}
```

- Do not animate repeated typing, navigation or keyboard actions.
- Respect `prefers-reduced-motion`.
- Use transform and opacity rather than layout properties.

---

## Anti-Patterns (Do NOT Use)

- ❌ Heavy chrome
- ❌ Slow response feedback
- ❌ AI replies inside rounded cards or chat bubbles
- ❌ Purple-on-purple AI gradients

### Additional Forbidden Patterns

- ❌ **Emojis as icons** — Use SVG icons (Heroicons, Lucide, Simple Icons)
- ❌ **Missing cursor:pointer** — All clickable elements must have cursor:pointer
- ❌ **Layout-shifting hovers** — Avoid scale transforms that shift layout
- ❌ **Low contrast text** — Maintain 4.5:1 minimum contrast ratio
- ❌ **Instant state changes** — Always use transitions (150-300ms)
- ❌ **Invisible focus states** — Focus states must be visible for a11y

---

## Pre-Delivery Checklist

Before delivering any UI code, verify:

- [ ] No emojis used as icons (use SVG instead)
- [ ] All icons from consistent icon set (Heroicons/Lucide)
- [ ] `cursor-pointer` on all clickable elements
- [ ] Hover states with smooth transitions (150-300ms)
- [ ] Light mode: text contrast 4.5:1 minimum
- [ ] Focus states visible for keyboard navigation
- [ ] `prefers-reduced-motion` respected
- [ ] Responsive: 375px, 768px, 1024px, 1440px
- [ ] No content hidden behind fixed navbars
- [ ] No horizontal scroll on mobile

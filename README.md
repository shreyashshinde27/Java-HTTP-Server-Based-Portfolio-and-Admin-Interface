# Shreyash's Portfolio (Static Site for GitHub Pages)

This branch contains the **static, frontend-only** version of Shreyash's portfolio website, designed for deployment on [GitHub Pages](https://pages.github.com/). All backend code and dependencies have been removed—only HTML, CSS, JavaScript, and assets remain.

## 📁 Project Structure

- `index.html` — Main portfolio page
- `success.html` — Contact form submission success page
- `js/` — Custom JavaScript (site logic, contact form handling, etc.)
- `css/` — Custom styles
- `bootstrap/` — Bootstrap CSS/JS (for layout and components)
- `assets/` — Images and other static assets

## ✉️ Contact Form (Frontend Email)
- The contact form uses [Formspree](https://formspree.io/) to send email notifications **without any backend**.
- To enable the form:
  1. Sign up at [Formspree](https://formspree.io/) and create a new form.
  2. Replace `YOUR_FORM_ID` in the `action` attribute of the contact form in `index.html` with your actual Formspree form ID.
  3. The form will send submissions directly to your email via Formspree.

## 🚀 Deployment
- This branch is ready for deployment on GitHub Pages.
- All files are in the root directory for easy publishing.

## ⚠️ No Backend
- There is **no backend** or server-side code in this branch.
- All features are implemented using static files and client-side JavaScript.

---

**Live Demo:** https://shreyashshinde27.github.io/MyPortfolio/
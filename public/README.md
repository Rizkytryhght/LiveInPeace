# LuxiQue - Luxury Fashion Landing Page

A modern, responsive landing page for LuxiQue luxury fashion mobile app built with React.js and Tailwind CSS.

## Features

- 🎨 Modern gradient design with smooth animations
- 📱 Fully responsive for all devices
- ⚡ Fast and optimized React components
- 🎭 Interactive hover effects and transitions
- 🧩 Modular component structure
- 📦 Easy to customize and extend

## Tech Stack

- **React.js** - Frontend framework
- **Tailwind CSS** - Utility-first CSS framework
- **Lucide React** - Beautiful icons
- **HTML5 & CSS3** - Semantic markup and styling

## Project Structure

```
luxique-app/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/
│   │   ├── Navbar.js          # Navigation component
│   │   ├── Hero.js            # Hero section with phone mockup
│   │   ├── Features.js        # Features showcase
│   │   ├── HowItWorks.js      # Step-by-step process
│   │   ├── Testimonials.js    # Customer reviews
│   │   ├── DownloadApp.js     # Download CTA section
│   │   └── Footer.js          # Footer with links
│   ├── styles/
│   │   └── index.css          # Global styles and animations
│   ├── App.js                 # Main app component
│   └── index.js               # App entry point
├── package.json
└── README.md
```

## Getting Started

### Prerequisites

- Node.js (v14 or higher)
- npm or yarn

### Installation

1. Clone or download the project files
2. Navigate to the project directory:
   ```bash
   cd luxique-app
   ```

3. Install dependencies:
   ```bash
   npm install
   ```

4. Start the development server:
   ```bash
   npm start
   ```

5. Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

### Building for Production

To create a production build:

```bash
npm run build
```

## Components Overview

### Navbar
- Fixed navigation with backdrop blur
- Mobile-responsive menu
- Smooth scroll links

### Hero
- Animated floating fashion icons
- Gradient text effects
- Interactive phone mockup
- Download buttons

### Features
- Three key features with icons
- Gradient backgrounds
- Hover animations

### HowItWorks
- Step-by-step process explanation
- Interactive numbered steps
- Phone mockup demonstration

### Testimonials
- Customer reviews with ratings
- Profile avatars
- Card hover effects

### DownloadApp
- Call-to-action section
- Gradient background
- Download buttons

### Footer
- Navigation links
- Social media icons
- Copyright information

## Customization

### Colors
Update colors in Tailwind classes throughout components:
- Primary: `purple-600` to `pink-600`
- Secondary: `blue-600` to `indigo-600`
- Accent: `green-600` to `teal-600`

### Content
Modify text content in each component file:
- Hero titles and descriptions
- Feature descriptions
- Testimonial content
- Step-by-step instructions

### Animations
Custom animations are defined in `src/styles/index.css`:
- Floating animation for icons
- Hover scale effects
- Gradient text effects

## Available Scripts

- `npm start` - Runs development server
- `npm run build` - Creates production build
- `npm test` - Runs test suite
- `npm run eject` - Ejects from Create React App

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

This project is open source and available under the MIT License.
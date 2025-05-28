import React from 'react';

const Footer = () => {
  const navLinks = [
    { name: 'Home', href: '#' },
    { name: 'Fitur', href: '#features' },
    { name: 'Cara Keja', href: '#how-it-works' },
    { name: 'Testimoni', href: '#testimonials' },
    { name: 'Unduh', href: '#download' }
  ];

  return (
    <footer className="bg-white py-16 px-4 border-t">
      <div className="max-w-7xl mx-auto">
        <div className="grid md:grid-cols-2 gap-8 items-center">
          {/* Logo */}
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-to-br from-green-600 to-emerald-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">L</span>
            </div>
            <span className="text-xl font-bold text-gray-900">Live In Peace</span>
          </div>

          {/* Navigation Links */}
          <div className="flex items-center justify-end space-x-8">
            {navLinks.map((link) => (
              <a 
                key={link.name}
                href={link.href} 
                className="text-gray-700 hover:text-green-600 transition-colors"
              >
                {link.name}
              </a>
            ))}
          </div>
        </div>

        {/* Bottom Section */}
        <div className="mt-12 pt-8 border-t border-gray-200 flex flex-col md:flex-row justify-between items-center">
          <p className="text-gray-600 text-sm">©2025 All Rights Reserved</p>
          <div className="flex items-center space-x-6 mt-4 md:mt-0">
            <a href="#" className="text-gray-600 hover:text-green-600 transition-colors text-sm">
              Terms and Privacy
            </a>
            <div className="flex space-x-4">
              <div className="w-8 h-8 bg-gray-900 rounded-full flex items-center justify-center hover:bg-gray-700 transition-colors cursor-pointer">
                <span className="text-white text-xs">f</span>
              </div>
              <div className="w-8 h-8 bg-gray-900 rounded-full flex items-center justify-center hover:bg-gray-700 transition-colors cursor-pointer">
                <span className="text-white text-xs">t</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
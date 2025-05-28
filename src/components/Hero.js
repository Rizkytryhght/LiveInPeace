import React from 'react';
import { Download, Play } from 'lucide-react';

const Hero = () => {
  const floatingIcons = [
    { emoji: '✨', position: 'top-10 left-10', rotation: 'rotate-12', delay: '0s' },
    { emoji: '📖', position: 'top-20 right-20', rotation: '-rotate-12', delay: '1s' },
    { emoji: '🧠', position: 'bottom-20 left-20', rotation: 'rotate-45', delay: '2s' },
    { emoji: '🌙', position: 'bottom-32 right-10', rotation: '-rotate-45', delay: '1.5s' }
  ];

  return (
    <section className="pt-32 pb-20 px-4 relative overflow-hidden">
      <div className="max-w-7xl mx-auto text-center relative">
        {/* Floating Fashion Icons */}
        <div className="absolute inset-0 pointer-events-none">
          {floatingIcons.map((icon, index) => (
            <div 
              key={index}
              className={`absolute ${icon.position} w-16 h-16 bg-gradient-to-br from-green-400 to-emerald-500 rounded-full flex items-center justify-center transform ${icon.rotation} animate-bounce`}
              style={{ animationDelay: icon.delay }}
            >
              <span className="text-white text-xl">{icon.emoji}</span>
            </div>
          ))}
        </div>

        {/* Content */}
        <div className="mb-6">
          <span className="text-green-600 font-medium">Temukan Ketenangan Batin</span>
        </div>
        
        <h1 className="text-5xl md:text-7xl font-bold text-gray-900 mb-6 leading-tight">
          Mulai Perjalanan
          <br />
          <span className="bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
            Kesejahteraan Jiwa Anda!
          </span>
        </h1>
        
        <p className="text-xl text-gray-600 mb-12 max-w-2xl mx-auto">
          Aplikasi kesehatan mental berbasis nilai-nilai Islami dan psikologi modern untuk mendukung kesejahteraan jiwa Anda.
        </p>

        {/* Download Buttons */}
        <div className="flex justify-center space-x-4 mb-16">
          <button className="bg-black text-white px-6 py-3 rounded-xl flex items-center space-x-2 hover:bg-gray-800 transition-all transform hover:scale-105">
            <Download size={20} />
            <span>App Store</span>
          </button>
          <button className="bg-black text-white px-6 py-3 rounded-xl flex items-center space-x-2 hover:bg-gray-800 transition-all transform hover:scale-105">
            <Play size={20} />
            <span>Google Play</span>
          </button>
        </div>

        {/* Phone Mockup */}
        <div className="relative max-w-md mx-auto transform hover:scale-105 transition-transform duration-500">
          <div className="relative bg-black rounded-[3rem] p-2 shadow-2xl">
            <div className="bg-white rounded-[2.5rem] overflow-hidden">
              <div className="h-8 bg-gray-100 flex items-center justify-center">
                <div className="w-16 h-1 bg-black rounded-full"></div>
              </div>
              <div className="p-6">
                <div className="flex items-center justify-between mb-6">
                  <span className="text-xl font-bold">Live In Peace</span>
                  <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
                </div>
                <div className="space-y-4">
                  <div className="bg-purple-100 rounded-xl p-4">
                    <div className="text-sm text-purple-600 mb-2">Fitur</div>
                    <div className="bg-white rounded-lg p-3 shadow-sm">
                      <div className="w-full h-32 bg-gradient-to-br from-green-200 to-green-400 rounded-lg mb-3 flex items-center justify-center">
                        <span className="text-2xl">📖</span>
                      </div>
                      <div className="text-sm font-medium">Pengingat</div>
                      <div className="text-xs text-gray-500">Gratis</div>
                    </div>
                  </div>
                  <div className="flex space-x-2">
                    <div className="w-10 h-10 bg-black rounded-full"></div>
                    <div className="w-10 h-10 bg-gray-300 rounded-full"></div>
                    <div className="w-10 h-10 bg-gray-300 rounded-full"></div>
                    <div className="w-10 h-10 bg-gray-300 rounded-full"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Hero;
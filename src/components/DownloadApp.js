import React from 'react';
import { Download, Play } from 'lucide-react';

const DownloadApp = () => {
  return (
    <section id="download" className="py-20 px-4 bg-gradient-to-br from-green-600 to-emerald-600 text-white">
      <div className="max-w-7xl mx-auto">
        <div className="grid lg:grid-cols-2 gap-16 items-center">
          {/* Left Content */}
          <div>
            <div className="mb-6">
              <span className="text-purple-200">Siap Menemukan Ketenangan?</span>
            </div>
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Unduh
              <br />
              Aplikasi Sekarang
            </h2>
            <p className="text-xl text-purple-100 mb-8">
              Unduh Live In Peace & mulai perjalanan kesejahteraan jiwa Anda!
            </p>
            <div className="flex space-x-4">
              <button className="bg-black text-white px-6 py-3 rounded-xl flex items-center space-x-2 hover:bg-gray-800 transition-all transform hover:scale-105">
                <Download size={20} />
                <span>App Store</span>
              </button>
              <button className="bg-black text-white px-6 py-3 rounded-xl flex items-center space-x-2 hover:bg-gray-800 transition-all transform hover:scale-105">
                <Play size={20} />
                <span>Google Play</span>
              </button>
            </div>
          </div>

          {/* Right Phone Mockup */}
          <div className="relative">
            <div className="relative bg-black rounded-[3rem] p-2 shadow-2xl transform hover:scale-105 transition-transform duration-500">
              <div className="bg-white rounded-[2.5rem] overflow-hidden">
                <div className="h-8 bg-gray-100 flex items-center justify-center">
                  <div className="w-16 h-1 bg-black rounded-full"></div>
                </div>
                <div className="p-6 text-gray-900">
                  <div className="flex items-center justify-between mb-6">
                    <span className="text-xl font-bold">LiveInPeace</span>
                    <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
                  </div>
                  <div className="space-y-4">
                    <div className="bg-purple-100 rounded-xl p-4">
                      <div className="text-sm text-purple-600 mb-2">Fitur</div>
                      <div className="bg-white rounded-lg p-3 shadow-sm">
                        <div className="w-full h-32 bg-gradient-to-br from-green-200 to-green-400 rounded-lg mb-3 flex items-center justify-center">
                          <span className="text-2xl">📖</span>
                        </div>
                        <div className="text-sm font-medium">Panduan Dzikir</div>
                        <div className="text-xs text-gray-500">Gratis</div>
                      </div>
                    </div>
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

export default DownloadApp;
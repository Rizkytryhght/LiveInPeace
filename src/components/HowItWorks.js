import React from 'react';

const HowItWorks = () => {
  const steps = [
    {
      number: "01",
      title: "Registrasi Mudah",
      description: "Buat akun Anda dan sesuaikan preferensi spiritual dan psikologis Anda."
    },
    {
      number: "02", 
      title: "Akses Fitur Islami",
      description: "Gunakan pengingat ibadah, dzikir, dan artikel psikologi Islami."
    },
    {
      number: "03",
      title: "Raih Kesejahteraan Jiwa", 
      description: "Bangun rutinitas sehat dan spiritual dengan dukungan harian dari aplikasi."
    }
  ];

  return (
    <section id="how-it-works" className="py-20 px-4">
      <div className="max-w-7xl mx-auto">
        <div className="grid lg:grid-cols-2 gap-16 items-center">
          {/* Left Content */}
          <div>
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-8">
              Langkah Mudah Menuju
              <br />
              Kesejahteraan
              <br />
              Jiwa Anda
            </h2>
            <p className="text-xl text-gray-600 mb-12">
              Temukan betapa mudahnya mencapai ketenangan batin dengan Live In Peace. Proses kami yang ramah pengguna memastikan pengalaman yang mulus.
            </p>

            <div className="space-y-8">
              {steps.map((step, index) => (
                <div key={index} className="flex items-start space-x-4 group">
                  <div className="w-12 h-12 bg-gradient-to-br from-green-600 to-emerald-600 rounded-xl flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform">
                    <span className="text-white font-bold">{step.number}</span>
                  </div>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 mb-2">{step.title}</h3>
                    <p className="text-gray-600">{step.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Right Phone Mockup */}
          <div className="relative">
            <div className="relative bg-black rounded-[3rem] p-2 shadow-2xl transform hover:scale-105 transition-transform duration-500">
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

export default HowItWorks;
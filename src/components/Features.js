import React from 'react';
import { Quran, Brain, Moon } from 'lucide-react';

const features = [
  { 
    icon: '✨',
    title: 'Konten Islami',
    description: 'Dzikir harian, ayat-ayat penyemangat, dan panduan ibadah untuk menenangkan hati.',
  },
  {
    icon: '👤',
    title: 'Psikologi Modern',
    description: 'Tips dan latihan dari psikolog untuk mengelola stres, kecemasan, dan emosi.',
  },
  {
    icon: '📱',
    title: 'Pengingat Ibadah',
    description: 'Notifikasi waktu dzikir dan salat untuk membentuk rutinitas spiritual yang sehat.',
  },
];

const Features = () => {
  return (
    <section className="py-20 px-4 bg-emerald-50">
      <div className="max-w-6xl mx-auto">
        <h2 className="text-3xl font-semibold text-center text-green-800 mb-10">Fitur Unggulan</h2>
        <div className="grid md:grid-cols-3 gap-10">
          {features.map((feature, index) => (
            <div key={index} className="bg-white rounded-xl shadow-md p-6 text-center">
              <div className="text-green-600 mb-4 flex justify-center">{feature.icon}</div>
              <h3 className="text-xl font-bold mb-2">{feature.title}</h3>
              <p className="text-gray-600">{feature.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Features;

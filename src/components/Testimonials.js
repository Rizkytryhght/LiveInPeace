import React from 'react';
import { Star } from 'lucide-react';

const Testimonials = () => {
  const testimonials = [
    {
      name: "Siddiq",
      rating: 5,
      text: "Aplikasi ini bantu aku lebih tenang dan rutin dzikir setiap pagi. Sangat membantu di masa-masa sulit"
    },
    {
      name: "Rizky", 
      rating: 5,
      text: "Live In Peace menggabungkan psikologi dan Islam dengan sangat baik. Suka banget fiturnya!"
    }
  ];

  const renderStars = (rating) => {
    return [...Array(rating)].map((_, i) => (
      <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
    ));
  };

  return (
    <section id="testimonials" className="py-20 px-4 bg-emerald/50">
      <div className="max-w-7xl mx-auto">
        <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
          Apa Kata
          <br />
          Pengguna
          <br />
          Live In Peace
        </h2>

        <div className="grid md:grid-cols-2 gap-8 mt-16">
          {testimonials.map((testimonial, index) => (
            <div key={index} className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-2">
              <div className="flex items-center mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-green-600 to-emerald-600 rounded-full flex items-center justify-center mr-4">
                  <span className="text-white font-bold">{testimonial.name[0]}</span>
                </div>
                <div>
                  <h4 className="font-bold text-gray-900">{testimonial.name}</h4>
                  <div className="flex">
                    {renderStars(testimonial.rating)}
                  </div>
                </div>
              </div>
              <p className="text-gray-600 leading-relaxed">{testimonial.text}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Testimonials;
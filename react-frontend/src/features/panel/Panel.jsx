import { Link } from 'react-router-dom';
import { CheckCircle, List, Layers, Play, Info } from 'lucide-react';

const Panel = () => {
    const Preguntaszes = [
        {
            id: 'vf',
            name: 'Verdadero / Falso',
            desc: 'Responde rápidamente con sí o no a afirmaciones generales.',
            icon: CheckCircle,
            color: 'text-emerald-600',
            bg: 'bg-emerald-50',
            path: '/preguntas/vf'
        },
        {
            id: 'sc',
            name: 'Selección Única',
            desc: 'Una única respuesta correcta entre varias opciones disponibles.',
            icon: List,
            color: 'text-sky-600',
            bg: 'bg-sky-50',
            path: '/preguntas/sc'
        },
        {
            id: 'mc',
            name: 'Selección Múltiple',
            desc: 'Cuidado: puede haber más de una respuesta correcta.',
            icon: Layers,
            color: 'text-violet-600',
            bg: 'bg-violet-50',
            path: '/preguntas/mc'
        },
    ];

    return (
        <div className="space-y-10 animate-in fade-in duration-500">
            <div className="text-center md:text-left">
                <h1 className="text-4xl font-extrabold text-gray-900 tracking-tight">Panel de Control</h1>
                <p className="text-lg text-gray-500 mt-2 max-w-2xl">
                    Pon a prueba tus conocimientos con nuestros cuestionarios optimizados.
                    Tus resultados se guardan automáticamente.
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                {Preguntaszes.map((Preguntas) => (
                    <div key={Preguntas.id} className="group relative bg-white p-8 rounded-3xl border border-gray-100 shadow-xl shadow-gray-200/50 hover:shadow-2xl hover:shadow-indigo-100/50 transition-all duration-300 transform hover:-translate-y-1">
                        <div className={`inline-flex items-center justify-center p-4 rounded-2xl ${Preguntas.bg} ${Preguntas.color} mb-6 transition-transform group-hover:scale-110`}>
                            <Preguntas.icon className="w-8 h-8" />
                        </div>
                        <h3 className="text-xl font-bold text-gray-900 mb-2">{Preguntas.name}</h3>
                        <p className="text-gray-500 text-sm mb-8 leading-relaxed">
                            {Preguntas.desc}
                        </p>
                        <Link
                            to={Preguntas.path}
                            className={`flex items-center justify-between w-full px-6 py-4 rounded-2xl font-bold text-sm transition-all ${Preguntas.bg} ${Preguntas.color} hover:brightness-95`}
                        >
                            <span>Comenzar Test</span>
                            <Play className="w-4 h-4 fill-current" />
                        </Link>
                    </div>
                ))}
            </div>

            <div className="bg-indigo-600 rounded-3xl p-8 md:p-12 text-white overflow-hidden relative shadow-2xl shadow-indigo-200">
                <div className="relative z-10 grid md:grid-cols-2 gap-8 items-center">
                    <div>
                        <div className="bg-indigo-400/30 w-12 h-12 rounded-2xl flex items-center justify-center mb-6">
                            <Info className="w-6 h-6 text-white" />
                        </div>
                        <h2 className="text-3xl font-bold mb-4">¿Cómo funciona?</h2>
                        <ul className="space-y-4">
                            {[
                                'Elige una de las categorías superiores para iniciar.',
                                'Tienes 5 preguntas por cada sesión de práctica.',
                                'El sistema evita mostrarte preguntas que ya has visto.',
                                'Revisa tu historial para ver tu progreso en el tiempo.'
                            ].map((text, i) => (
                                <li key={i} className="flex items-start gap-3">
                                    <div className="mt-1.5 w-1.5 h-1.5 rounded-full bg-indigo-300 shrink-0" />
                                    <span className="text-indigo-50 font-medium">{text}</span>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
                {/* Subtle background decoration */}
                <div className="absolute top-0 right-0 -mr-20 -mt-20 w-96 h-96 bg-indigo-500 rounded-full blur-3xl opacity-50" />
            </div>
        </div>
    );
};

export default Panel;

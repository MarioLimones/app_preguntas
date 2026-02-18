import { Link } from 'react-router-dom';
import { CheckCircle, List, Layers, Play } from 'lucide-react';

const Dashboard = () => {
    const quizzes = [
        { id: 'vf', name: 'Verdadero / Falso', icon: CheckCircle, color: 'text-green-600', bg: 'bg-green-100', path: '/quiz/vf' },
        { id: 'sc', name: 'Selección Única', icon: List, color: 'text-blue-600', bg: 'bg-blue-100', path: '/quiz/sc' },
        { id: 'mc', name: 'Selección Múltiple', icon: Layers, color: 'text-purple-600', bg: 'bg-purple-100', path: '/quiz/mc' },
    ];

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold text-gray-900">Bienvenido al Quiz</h1>
            <p className="text-gray-600">Selecciona un tipo de cuestionario para comenzar a practicar.</p>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
                {quizzes.map((quiz) => (
                    <div key={quiz.id} className="bg-white overflow-hidden rounded-lg shadow hover:shadow-md transition-shadow duration-300 cursor-pointer">
                        <div className="p-5">
                            <div className="flex items-center">
                                <div className={`flex-shrink-0 rounded-md p-3 ${quiz.bg}`}>
                                    <quiz.icon className={`h-6 w-6 ${quiz.color}`} aria-hidden="true" />
                                </div>
                                <div className="ml-5 w-0 flex-1">
                                    <dt className="text-sm font-medium text-gray-500 truncate">{quiz.name}</dt>
                                    <dd>
                                        <div className="text-lg font-medium text-gray-900 mt-1">Practicar ahora</div>
                                    </dd>
                                </div>
                            </div>
                        </div>
                        <div className="bg-gray-50 px-5 py-3">
                            <div className="text-sm">
                                <Link to={quiz.path} className="font-medium text-indigo-600 hover:text-indigo-500 flex items-center">
                                    <Play className="w-4 h-4 mr-1" />
                                    Comenzar Test
                                </Link>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className="mt-10 bg-white p-6 rounded-lg shadow-sm border border-gray-200">
                <h2 className="text-xl font-bold text-gray-800 mb-4">Instrucciones</h2>
                <ul className="list-disc list-inside space-y-2 text-gray-700">
                    <li>Selecciona una categoría arriba para iniciar una sesión de preguntas.</li>
                    <li>Cada sesión consta de preguntas aleatorias.</li>
                    <li>Al finalizar, podrás ver tu puntuación y revisar tus respuestas.</li>
                    <li>Tus resultados quedarán guardados en tu historial.</li>
                </ul>
            </div>
        </div>
    );
};

export default Dashboard;

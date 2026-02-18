import { useState, useEffect } from 'react';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import { Calendar, CheckCircle } from 'lucide-react';

const History = () => {
    const { user } = useAuth();
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchResults = async () => {
            try {
                const response = await api.get(`/results?username=${user.username}`);
                setResults(response.data);
            } catch (error) {
                console.error("Error fetching results", error);
            } finally {
                setLoading(false);
            }
        };
        fetchResults();
    }, [user.username]);

    if (loading) return <div className="text-center p-10">Cargando...</div>;

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-2xl font-bold mb-6 text-gray-800">Historial de Resultados</h1>

            {results.length === 0 ? (
                <div className="bg-white p-8 rounded-lg shadow text-center text-gray-500">
                    No tienes resultados registrados aún.
                </div>
            ) : (
                <div className="grid gap-4">
                    {results.map((result) => (
                        <div key={result.id} className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow">
                            <div className="flex justify-between items-start">
                                <div>
                                    <div className="flex items-center gap-2 mb-2">
                                        <span className={`px-2 py-1 rounded text-xs font-bold ${result.quizType === 'VF' ? 'bg-green-100 text-green-800' :
                                                result.quizType === 'SC' ? 'bg-blue-100 text-blue-800' :
                                                    'bg-purple-100 text-purple-800'
                                            }`}>
                                            {result.quizType}
                                        </span>
                                        <span className="text-sm text-gray-500 flex items-center">
                                            <Calendar className="w-3 h-3 mr-1" />
                                            {new Date(result.completedAt).toLocaleString()}
                                        </span>
                                    </div>
                                    <p className="text-gray-600 text-sm">
                                        {result.correctAnswers} correctas de {result.totalQuestions}
                                    </p>
                                </div>
                                <div className="text-right">
                                    <div className={`text-2xl font-bold ${result.scorePercentage >= 60 ? 'text-green-600' : 'text-red-600'}`}>
                                        {result.scorePercentage.toFixed(0)}%
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default History;

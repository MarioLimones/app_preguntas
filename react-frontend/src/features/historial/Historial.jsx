import { useState, useEffect } from 'react';
import api from '../../core/api/client';
import { useAuth } from '../../core/autenticacion/AuthContext';
import { Calendar, CheckCircle, Clock, Trophy, ChevronRight } from 'lucide-react';

const Historial = () => {
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

    if (loading) return (
        <div className="flex justify-center items-center h-64">
            <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
        </div>
    );

    return (
        <div className="max-w-4xl mx-auto animate-in fade-in duration-500">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-10">
                <div>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Tu Progreso</h1>
                    <p className="text-gray-500 mt-1">Revisa el historial de tus desafíos completados.</p>
                </div>
                <div className="bg-white px-6 py-4 rounded-3xl shadow-sm border border-gray-100 flex items-center gap-4">
                    <div className="bg-indigo-50 p-3 rounded-2xl">
                        <Trophy className="w-6 h-6 text-indigo-600" />
                    </div>
                    <div>
                        <p className="text-xs font-bold text-gray-400 uppercase tracking-widest">Tests Totales</p>
                        <p className="text-xl font-black text-gray-900">{results.length}</p>
                    </div>
                </div>
            </div>

            {results.length === 0 ? (
                <div className="bg-white p-12 rounded-[2.5rem] shadow-xl text-center border border-gray-50">
                    <Calendar className="w-16 h-16 text-gray-200 mx-auto mb-4" />
                    <h3 className="text-xl font-bold text-gray-800">No hay registros aún</h3>
                    <p className="text-gray-500 mt-2 max-w-xs mx-auto">Cuando completes tu primer test aparecerá aquí con tus estadísticas detalladas.</p>
                </div>
            ) : (
                <div className="space-y-4">
                    {results.sort((a, b) => new Date(b.completedAt) - new Date(a.completedAt)).map((result) => (
                        <div key={result.id} className="group bg-white p-6 rounded-[2rem] shadow-sm border border-gray-100 hover:border-indigo-100 hover:shadow-xl hover:shadow-indigo-50 transition-all duration-300">
                            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6">
                                <div className="flex items-center gap-5">
                                    <div className={`shrink-0 w-14 h-14 rounded-2xl flex items-center justify-center font-black text-xs ${result.PreguntasType === 'VF' ? 'bg-emerald-50 text-emerald-600' :
                                            result.PreguntasType === 'SC' ? 'bg-sky-50 text-sky-600' :
                                                'bg-violet-50 text-violet-600'
                                        }`}>
                                        {result.PreguntasType}
                                    </div>
                                    <div>
                                        <div className="flex items-center gap-2 mb-1">
                                            <span className="text-sm font-bold text-gray-900">
                                                {result.PreguntasType === 'VF' ? 'Verdadero o Falso' : result.PreguntasType === 'SC' ? 'Selección Única' : 'Selección Múltiple'}
                                            </span>
                                            <div className="w-1 h-1 rounded-full bg-gray-300" />
                                            <span className="text-[10px] text-gray-400 font-bold flex items-center gap-1">
                                                <Clock className="w-3 h-3" />
                                                {new Date(result.completedAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <p className="text-xs text-gray-400 font-medium">
                                            Respondiste correctamente <span className="text-gray-700 font-bold">{result.correctAnswers}</span> de <span className="text-gray-700 font-bold">{result.totalQuestions}</span>
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-6 self-end sm:self-auto">
                                    <div className="text-right">
                                        <div className={`text-3xl font-black ${result.scorePercentage >= 60 ? 'text-emerald-500' : 'text-rose-500'}`}>
                                            {result.scorePercentage.toFixed(0)}<span className="text-lg">%</span>
                                        </div>
                                        <p className="text-[10px] uppercase font-black text-gray-300 tracking-widest">{result.scorePercentage >= 60 ? 'Aprobado' : 'Fallido'}</p>
                                    </div>
                                    <div className="w-10 h-10 rounded-full border border-gray-50 flex items-center justify-center text-gray-300 group-hover:text-indigo-600 group-hover:bg-indigo-50 group-hover:border-indigo-100 transition-all">
                                        <ChevronRight className="w-5 h-5" />
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

export default Historial;

import { useState, useEffect } from 'react';
import api from '../../core/api/client';
import { useAuth } from '../../core/auth/AuthContext';
import { Calendar, CheckCircle, Clock, Trophy, ChevronRight } from 'lucide-react';

const Historial = () => {
    const { user } = useAuth();
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [expandedId, setExpandedId] = useState(null);

    const toggleExpand = (id) => {
        setExpandedId(expandedId === id ? null : id);
    };

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
                    {results.sort((a, b) => new Date(b.completedAt) - new Date(a.completedAt)).map((result) => {
                        const quizType = result.quizType || result.PreguntasType || '';
                        return (
                            <div key={result.id} className="group bg-white rounded-[2rem] shadow-sm border border-gray-100 hover:border-indigo-100 hover:shadow-xl hover:shadow-indigo-50 transition-all duration-300 cursor-pointer overflow-hidden" onClick={() => toggleExpand(result.id)}>
                                <div className="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-6">
                                    <div className="flex items-center gap-5">
                                        <div className={`shrink-0 w-14 h-14 rounded-2xl flex items-center justify-center font-black text-xs ${quizType === 'VF' ? 'bg-emerald-50 text-emerald-600' :
                                            quizType === 'SC' ? 'bg-sky-50 text-sky-600' :
                                                'bg-violet-50 text-violet-600'
                                            }`}>
                                            {quizType}
                                        </div>
                                        <div>
                                            <div className="flex items-center gap-2 mb-1">
                                                <span className="text-sm font-bold text-gray-900">
                                                    {quizType === 'VF' ? 'Verdadero o Falso' : quizType === 'SC' ? 'Selección Única' : 'Selección Múltiple'}
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
                                        <div className={`w-10 h-10 rounded-full border border-gray-50 flex items-center justify-center text-gray-300 group-hover:text-indigo-600 group-hover:bg-indigo-50 group-hover:border-indigo-100 transition-all ${expandedId === result.id ? 'rotate-90 bg-indigo-50 text-indigo-600 border-indigo-100' : ''}`}>
                                            <ChevronRight className="w-5 h-5 transition-transform" />
                                        </div>
                                    </div>
                                </div>

                                {expandedId === result.id && result.details && result.details.length > 0 && (
                                    <div className="px-6 pb-6 pt-2 border-t border-gray-50 bg-gray-50/50 animate-in slide-in-from-top-2 duration-300">
                                        <h3 className="text-sm font-bold text-gray-900 mb-4 px-2 tracking-wide">Detalles de Tareas</h3>
                                        <div className="space-y-3">
                                            {result.details.map((detail, index) => (
                                                <div key={index} className={`p-4 rounded-xl border ${detail.correct ? 'bg-emerald-50/50 border-emerald-100 ' : 'bg-rose-50/50 border-rose-100'}`}>
                                                    <div className="flex items-start gap-4">
                                                        <div className="mt-0.5 shrink-0">
                                                            {detail.correct ? (
                                                                <CheckCircle className="w-5 h-5 text-emerald-500" />
                                                            ) : (
                                                                <div className="w-5 h-5 flex items-center justify-center rounded-full bg-rose-200 text-rose-600 font-bold text-xs ring-2 ring-white">✕</div>
                                                            )}
                                                        </div>
                                                        <div className="flex-1 min-w-0">
                                                            <p className="text-sm font-semibold text-gray-800 mb-3 leading-relaxed">{detail.statement}</p>
                                                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                                                                <div className="bg-white/90 p-3 rounded-lg border border-gray-100 shadow-sm">
                                                                    <span className="text-gray-400 block mb-1.5 font-medium uppercase tracking-wider text-[10px]">Tu respuesta</span>
                                                                    <span className={`font-semibold block truncate ${detail.correct ? 'text-emerald-700' : 'text-rose-700'}`}>
                                                                        {detail.userAnswer || 'Sin responder'}
                                                                    </span>
                                                                </div>
                                                                {!detail.correct && (
                                                                    <div className="bg-emerald-50/80 p-3 rounded-lg border border-emerald-100 shadow-sm">
                                                                        <span className="text-emerald-600/70 block mb-1.5 font-medium uppercase tracking-wider text-[10px]">Respuesta correcta</span>
                                                                        <span className="font-semibold text-emerald-700 block truncate">
                                                                            {detail.correctAnswer}
                                                                        </span>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default Historial;


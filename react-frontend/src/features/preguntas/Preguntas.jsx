import { useState, useEffect, useCallback, useMemo, memo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../core/api/client';
import { useAuth } from '../../core/autenticacion/AuthContext';
import { Check, X, ArrowRight, RotateCcw, Award, HelpCircle } from 'lucide-react';

const QUESTIONS_PER_PREGUNTAS = 5;

// Sub-componentes memoizados para evitar re-renders innecesarios
const QuestionHeader = memo(({ currentIndex, totalQuestions, typeLabel }) => (
    <div className="mb-6 flex items-center justify-between">
        <div className="bg-white px-4 py-2 rounded-2xl shadow-sm border border-gray-100 text-sm font-bold text-gray-500">
            Pregunta <span className="text-indigo-600">{currentIndex + 1}</span> de {totalQuestions}
        </div>
        <div className="bg-indigo-50 px-4 py-2 rounded-2xl text-xs font-black text-indigo-600 uppercase tracking-widest">
            {typeLabel}
        </div>
    </div>
));

const ResultItem = memo(({ q, idx, isCorrect, type, explanation }) => (
    <div className={`p-6 rounded-2xl border-2 transition-colors ${isCorrect ? 'bg-emerald-50/50 border-emerald-100' : 'bg-rose-50/50 border-rose-100'}`}>
        <div className="flex items-start gap-4">
            <div className={`shrink-0 w-8 h-8 rounded-xl flex items-center justify-center mt-0.5 ${isCorrect ? 'bg-emerald-500 text-white' : 'bg-rose-500 text-white'}`}>
                {isCorrect ? <Check className="w-5 h-5" /> : <X className="w-5 h-5" />}
            </div>
            <div className="flex-1">
                <p className="text-gray-900 font-bold mb-2">{idx + 1}. {q.statement}</p>
                {!isCorrect && (
                    <div className="text-sm bg-white/50 p-3 rounded-xl border border-rose-100 mt-2">
                        <span className="text-rose-600 font-bold block mb-1">Respuesta correcta:</span>
                        <span className="text-rose-500">
                            {type === 'vf' ? (q.correctAnswer ? 'Verdadero' : 'Falso') :
                                type === 'sc' ? q.options[q.correctIndex] :
                                    type === 'mc' ? q.correctIndexes.map(i => q.options[i]).join(', ') : ''}
                        </span>
                    </div>
                )}
                {q.explanation && (
                    <p className="text-xs text-gray-500 mt-3 italic bg-gray-50 p-2 rounded-lg">{q.explanation}</p>
                )}
            </div>
        </div>
    </div>
));

const Preguntas = () => {
    const { type } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [questions, setQuestions] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [userAnswers, setUserAnswers] = useState({});
    const [loading, setLoading] = useState(true);
    const [finished, setFinished] = useState(false);
    const [score, setScore] = useState(0);
    const [saving, setSaving] = useState(false);

    const typeLabel = useMemo(() => {
        if (type === 'vf') return 'Verdadero o Falso';
        if (type === 'sc') return 'Selección Única';
        if (type === 'mc') return 'Selección Múltiple';
        return '';
    }, [type]);

    const fetchQuestions = useCallback(async () => {
        setLoading(true);
        try {
            const storageKey = `Preguntas_queue_${type}`;
            let queue = [];

            try {
                const stored = localStorage.getItem(storageKey);
                if (stored) queue = JSON.parse(stored);
            } catch (e) {
                console.error("Error parsing stored queue", e);
            }

            const response = await api.get(`/${type}/preguntas`);
            const allFetched = response.data || [];

            // DEDUP by statement de forma eficiente
            const uniqueQuestionsMap = new Map();
            allFetched.forEach(q => {
                if (!uniqueQuestionsMap.has(q.statement)) {
                    uniqueQuestionsMap.set(q.statement, q);
                }
            });
            const uniqueQuestionsList = Array.from(uniqueQuestionsMap.values());
            const uniqueIds = uniqueQuestionsList.map(q => q.id);

            queue = queue.filter(id => uniqueIds.includes(id));

            if (queue.length < QUESTIONS_PER_PREGUNTAS) {
                const shuffledIds = [...uniqueIds].sort(() => Math.random() - 0.5);
                const nextOnes = shuffledIds.filter(id => !queue.includes(id));
                queue = [...queue, ...nextOnes];
            }

            const selectedIds = queue.slice(0, Math.min(QUESTIONS_PER_PREGUNTAS, queue.length));
            const remainingQueue = queue.slice(selectedIds.length);
            localStorage.setItem(storageKey, JSON.stringify(remainingQueue));

            const selectedQuestions = selectedIds
                .map(id => uniqueQuestionsMap.get(uniqueQuestionsList.find(q => q.id === id)?.statement))
                .filter(Boolean);

            setQuestions(selectedQuestions);
        } catch (error) {
            console.error("Error fetching questions", error);
        } finally {
            setLoading(false);
        }
    }, [type]);

    useEffect(() => {
        fetchQuestions();
    }, [fetchQuestions]);

    const calculateCorrectness = useCallback((question, answer) => {
        if (!question) return false;
        if (type === 'vf') return question.correctAnswer === answer;
        if (type === 'sc') return question.correctIndex === answer;
        if (type === 'mc') {
            const correctObj = new Set(question.correctIndexes);
            const answerObj = new Set(answer || []);
            if (correctObj.size !== answerObj.size) return false;
            for (let a of answerObj) if (!correctObj.has(a)) return false;
            return true;
        }
        return false;
    }, [type]);

    const handleAnswer = useCallback((answer) => {
        setUserAnswers(prev => ({
            ...prev,
            [questions[currentIndex].id]: answer
        }));
    }, [currentIndex, questions]);

    const finishPreguntas = useCallback(async () => {
        let optionsCorrect = 0;
        const resultDetails = questions.map(q => {
            const userAnswer = userAnswers[q.id];
            const isCorrect = calculateCorrectness(q, userAnswer);
            if (isCorrect) optionsCorrect++;

            let uaStr = '';
            let caStr = '';

            if (type === 'vf') {
                uaStr = userAnswer === true ? 'Verdadero' : (userAnswer === false ? 'Falso' : 'Sin respuesta');
                caStr = q.correctAnswer ? 'Verdadero' : 'Falso';
            } else if (type === 'sc') {
                uaStr = q.options[userAnswer] || 'Sin respuesta';
                caStr = q.options[q.correctIndex];
            } else if (type === 'mc') {
                uaStr = (userAnswer || []).map(i => q.options[i]).join(', ') || 'Sin respuesta';
                caStr = (q.correctIndexes || []).map(i => q.options[i]).join(', ');
            }

            return {
                questionId: q.id,
                statement: q.statement,
                correct: isCorrect,
                userAnswer: uaStr,
                correctAnswer: caStr
            };
        });

        const percentage = (optionsCorrect / questions.length) * 100;
        setScore(percentage);
        setFinished(true);

        setSaving(true);
        try {
            await api.post('/results', {
                username: user.username,
                PreguntasType: type.toUpperCase(),
                totalQuestions: questions.length,
                correctAnswers: optionsCorrect,
                scorePercentage: percentage,
                details: resultDetails
            });
        } catch (error) {
            console.error("Error saving results", error);
        } finally {
            setSaving(false);
        }
    }, [questions, userAnswers, calculateCorrectness, type, user.username]);

    const handleNext = useCallback(() => {
        if (currentIndex < questions.length - 1) {
            setCurrentIndex(prev => prev + 1);
        } else {
            finishPreguntas();
        }
    }, [currentIndex, questions.length, finishPreguntas]);

    if (loading) return (
        <div className="flex flex-col items-center justify-center min-h-[400px] gap-4">
            <div className="w-12 h-12 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
            <p className="text-gray-500 font-medium">Preparando tus preguntas...</p>
        </div>
    );

    if (questions.length === 0) return (
        <div className="text-center p-12 bg-white rounded-3xl shadow-xl mt-12">
            <HelpCircle className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-gray-800">Ups, no hay preguntas</h2>
            <p className="text-gray-500 mt-2">No se encontraron preguntas para esta categoría en la base de datos.</p>
            <button onClick={() => navigate('/')} className="mt-6 px-6 py-3 bg-indigo-600 text-white rounded-2xl font-bold hover:bg-indigo-700 transition-colors">Volver al inicio</button>
        </div>
    );

    if (finished) {
        return (
            <div className="max-w-2xl mx-auto bg-white p-8 md:p-12 rounded-3xl shadow-2xl animate-in zoom-in-95 duration-300">
                <div className="text-center mb-10">
                    <div className="inline-flex items-center justify-center w-20 h-20 bg-indigo-50 rounded-3xl mb-4">
                        <Award className="w-10 h-10 text-indigo-600" />
                    </div>
                    <h2 className="text-3xl font-bold text-gray-900">¡Test Completado!</h2>
                    <div className={`text-6xl font-black mt-4 flex items-center justify-center gap-2 ${score >= 60 ? 'text-emerald-500' : 'text-rose-500'}`}>
                        {score.toFixed(0)}<span className="text-3xl">%</span>
                    </div>
                </div>

                <div className="space-y-4 mb-10">
                    {questions.map((q, idx) => (
                        <ResultItem 
                            key={q.id} 
                            q={q} 
                            idx={idx} 
                            isCorrect={calculateCorrectness(q, userAnswers[q.id])} 
                            type={type} 
                        />
                    ))}
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <button onClick={() => navigate('/')} className="px-6 py-4 bg-gray-100 text-gray-600 font-bold rounded-2xl hover:bg-gray-200 transition-all">
                        Ir al Inicio
                    </button>
                    <button onClick={() => window.location.reload()} className="px-6 py-4 bg-indigo-600 text-white font-bold rounded-2xl hover:bg-indigo-700 shadow-lg shadow-indigo-100 flex items-center justify-center gap-2 transition-all active:scale-95">
                        <RotateCcw className="w-5 h-5" /> Repetir Test
                    </button>
                </div>
            </div>
        );
    }

    const currentQ = questions[currentIndex];
    const currentAnswer = userAnswers[currentQ.id];

    const isAnswered = () => {
        if (type === 'mc') return Array.isArray(currentAnswer) && currentAnswer.length > 0;
        return currentAnswer !== undefined && currentAnswer !== null;
    };

    return (
        <div className="max-w-3xl mx-auto animate-in fade-in duration-500">
            <QuestionHeader 
                currentIndex={currentIndex} 
                totalQuestions={questions.length} 
                typeLabel={typeLabel} 
            />

            <div className="bg-white p-8 md:p-12 rounded-[2.5rem] shadow-2xl shadow-indigo-100/30 min-h-[400px] flex flex-col border border-indigo-50">
                <h2 className="text-2xl font-bold mb-10 text-gray-900 leading-tight">{currentQ.statement}</h2>

                <div className="flex-1 space-y-4">
                    {type === 'vf' && (
                        <div className="grid grid-cols-2 gap-4">
                            {[true, false].map((opt) => (
                                <button
                                    key={opt.toString()}
                                    onClick={() => handleAnswer(opt)}
                                    className={`py-6 border-4 rounded-3xl font-black text-lg transition-all ${currentAnswer === opt
                                        ? 'border-indigo-600 bg-indigo-600 text-white shadow-xl shadow-indigo-200'
                                        : 'border-gray-50 bg-gray-50 text-gray-400 hover:border-indigo-100 hover:bg-indigo-50/50 hover:text-indigo-400'
                                        }`}
                                >
                                    {opt ? 'VERDADERO' : 'FALSO'}
                                </button>
                            ))}
                        </div>
                    )}

                    {type === 'sc' && (
                        <div className="space-y-3">
                            {currentQ.options.map((opt, idx) => (
                                <button
                                    key={idx}
                                    onClick={() => handleAnswer(idx)}
                                    className={`w-full text-left px-6 py-5 border-2 rounded-2xl font-bold transition-all flex items-center gap-4 ${currentAnswer === idx
                                        ? 'bg-indigo-600 border-indigo-600 text-white shadow-lg shadow-indigo-100'
                                        : 'bg-white border-gray-100 text-gray-600 hover:border-indigo-200 hover:bg-indigo-50/30'
                                        }`}
                                >
                                    <div className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 text-sm ${currentAnswer === idx ? 'bg-white/20' : 'bg-gray-100 text-gray-400'}`}>
                                        {String.fromCharCode(65 + idx)}
                                    </div>
                                    {opt}
                                </button>
                            ))}
                        </div>
                    )}

                    {type === 'mc' && (
                        <div className="space-y-3">
                            {currentQ.options.map((opt, idx) => {
                                const selected = (currentAnswer || []).includes(idx);
                                const toggle = () => {
                                    const prev = currentAnswer || [];
                                    if (selected) handleAnswer(prev.filter(i => i !== idx));
                                    else handleAnswer([...prev, idx]);
                                };
                                return (
                                    <button
                                        key={idx}
                                        onClick={toggle}
                                        className={`w-full text-left px-6 py-5 border-2 rounded-2xl font-bold transition-all flex items-center gap-4 ${selected
                                            ? 'bg-indigo-600 border-indigo-600 text-white shadow-lg shadow-indigo-100'
                                            : 'bg-white border-gray-100 text-gray-600 hover:border-indigo-200 hover:bg-indigo-50/30'
                                            }`}
                                    >
                                        <div className={`w-6 h-6 border-2 rounded-lg flex items-center justify-center shrink-0 ${selected ? 'bg-white border-white text-indigo-600' : 'bg-transparent border-gray-200'}`}>
                                            {selected && <Check className="w-4 h-4 font-black" />}
                                        </div>
                                        {opt}
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>

                <div className="mt-12 flex justify-end">
                    <button
                        onClick={handleNext}
                        disabled={!isAnswered()}
                        className={`px-10 py-4 rounded-2xl font-black text-sm uppercase tracking-widest flex items-center gap-2 transition-all active:scale-95 ${isAnswered()
                            ? 'bg-indigo-600 text-white hover:bg-indigo-700 shadow-xl shadow-indigo-200'
                            : 'bg-gray-100 text-gray-300 cursor-not-allowed'
                            }`}
                    >
                        {currentIndex === questions.length - 1 ? 'Finalizar' : 'Siguiente'}
                        <ArrowRight className="w-5 h-5" />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default memo(Preguntas);

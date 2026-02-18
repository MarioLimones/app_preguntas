import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import { Check, X, ArrowRight, Save, RotateCcw } from 'lucide-react';

const QUESTIONS_PER_QUIZ = 5;

const Quiz = () => {
    const { type } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [questions, setQuestions] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [userAnswers, setUserAnswers] = useState({}); // { questionId: answer }
    const [loading, setLoading] = useState(true);
    const [finished, setFinished] = useState(false);
    const [score, setScore] = useState(0);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        fetchQuestions();
    }, [type]);

    const fetchQuestions = async () => {
        setLoading(true);
        try {
            // Fetch multiple random questions in parallel
            const promises = Array.from({ length: QUESTIONS_PER_QUIZ }).map(() =>
                api.get(`/api/${type}/questions/random`)
            );
            const results = await Promise.all(promises);
            // Dedup by ID if possible, though unlikely with small sample size/large pool
            const fetched = results.map(r => r.data).filter(q => q);
            // Filter unique IDs
            const unique = [];
            const ids = new Set();
            for (const item of fetched) {
                if (!ids.has(item.id)) {
                    unique.push(item);
                    ids.add(item.id);
                }
            }
            setQuestions(unique);
        } catch (error) {
            console.error("Error fetching questions", error);
        } finally {
            setLoading(false);
        }
    };

    const handleAnswer = (answer) => {
        setUserAnswers(prev => ({
            ...prev,
            [questions[currentIndex].id]: answer
        }));
    };

    const handleNext = () => {
        if (currentIndex < questions.length - 1) {
            setCurrentIndex(currentIndex + 1);
        } else {
            finishQuiz();
        }
    };

    const calculateCorrectness = (question, answer) => {
        if (type === 'vf') {
            // answer is boolean
            return question.correctAnswer === answer;
        } else if (type === 'sc') {
            // answer is index
            return question.correctIndex === answer;
        } else if (type === 'mc') {
            // answer is array of indexes
            // Sort both and compare
            const correctObj = new Set(question.correctIndexes);
            const answerObj = new Set(answer);
            if (correctObj.size !== answerObj.size) return false;
            for (let a of answerObj) if (!correctObj.has(a)) return false;
            return true;
        }
        return false;
    };

    const finishQuiz = async () => {
        let optionsCorrect = 0;
        const resultDetails = questions.map(q => {
            const userAnswer = userAnswers[q.id];
            const isCorrect = calculateCorrectness(q, userAnswer);
            if (isCorrect) optionsCorrect++;

            // Format answer string for DB
            let uaStr = '';
            let caStr = '';

            if (type === 'vf') {
                uaStr = userAnswer ? 'Verdadero' : 'Falso';
                caStr = q.correctAnswer ? 'Verdadero' : 'Falso';
            } else if (type === 'sc') {
                uaStr = q.options[userAnswer];
                caStr = q.options[q.correctIndex];
            } else if (type === 'mc') {
                uaStr = (userAnswer || []).map(i => q.options[i]).join(', ');
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

        // Save result
        setSaving(true);
        try {
            await api.post('/results', {
                username: user.username,
                quizType: type.toUpperCase(),
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
    };

    if (loading) return <div className="text-center mt-20 text-xl text-gray-600">Cargando preguntas...</div>;
    if (questions.length === 0) return <div className="text-center mt-20 text-xl text-gray-600">No se encontraron preguntas.</div>;

    if (finished) {
        return (
            <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-gray-800">Resultados</h2>
                    <div className={`text-5xl font-bold mt-4 ${score >= 60 ? 'text-green-600' : 'text-red-600'}`}>
                        {score.toFixed(0)}%
                    </div>
                    <p className="text-gray-600 mt-2">
                        Has respondido correctamente {Math.round((score / 100) * questions.length)} de {questions.length}
                    </p>
                </div>

                <div className="space-y-4 mb-8">
                    {questions.map((q, idx) => {
                        const isCorrect = calculateCorrectness(q, userAnswers[q.id]);
                        return (
                            <div key={q.id} className={`p-4 rounded border ${isCorrect ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'}`}>
                                <div className="flex items-start">
                                    <div className="flex-shrink-0 mt-1">
                                        {isCorrect ? <Check className="w-5 h-5 text-green-600" /> : <X className="w-5 h-5 text-red-600" />}
                                    </div>
                                    <div className="ml-3">
                                        <p className="text-sm font-medium text-gray-900">{idx + 1}. {q.statement}</p>
                                        {!isCorrect && (
                                            <p className="text-xs text-red-600 mt-1">
                                                Respuesta correcta: {
                                                    type === 'vf' ? (q.correctAnswer ? 'Verdadero' : 'Falso') :
                                                        type === 'sc' ? q.options[q.correctIndex] :
                                                            type === 'mc' ? q.correctIndexes.map(i => q.options[i]).join(', ') : ''
                                                }
                                            </p>
                                        )}
                                        {q.explanation && (
                                            <p className="text-xs text-gray-500 mt-1 italic">{q.explanation}</p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>

                <div className="flex justify-center gap-4">
                    <button onClick={() => navigate('/')} className="px-6 py-2 bg-gray-600 text-white rounded hover:bg-gray-700">
                        Volver al Inicio
                    </button>
                    <button onClick={() => window.location.reload()} className="px-6 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 flex items-center">
                        <RotateCcw className="w-4 h-4 mr-2" /> Intentar de nuevo
                    </button>
                </div>
            </div>
        );
    }

    const currentQ = questions[currentIndex];
    const currentAnswer = userAnswers[currentQ.id];

    return (
        <div className="max-w-3xl mx-auto">
            <div className="mb-4 flex justify-between text-sm text-gray-500">
                <span>Pregunta {currentIndex + 1} de {questions.length}</span>
                <span>Tipo: {type.toUpperCase()}</span>
            </div>

            <div className="bg-white p-8 rounded-lg shadow-md min-h-[300px] flex flex-col">
                <h2 className="text-xl font-semibold mb-6 text-gray-800">{currentQ.statement}</h2>

                <div className="flex-1 space-y-3">
                    {type === 'vf' && (
                        <div className="flex gap-4">
                            {[true, false].map((opt) => (
                                <button
                                    key={opt.toString()}
                                    onClick={() => handleAnswer(opt)}
                                    className={`flex-1 py-4 border-2 rounded-lg font-medium transition-colors ${currentAnswer === opt
                                            ? 'border-indigo-600 bg-indigo-50 text-indigo-700'
                                            : 'border-gray-200 hover:border-indigo-300'
                                        }`}
                                >
                                    {opt ? 'Verdadero' : 'Falso'}
                                </button>
                            ))}
                        </div>
                    )}

                    {type === 'sc' && (
                        <div className="space-y-2">
                            {currentQ.options.map((opt, idx) => (
                                <button
                                    key={idx}
                                    onClick={() => handleAnswer(idx)}
                                    className={`w-full text-left p-4 border rounded-lg hover:bg-gray-50 ${currentAnswer === idx ? 'bg-indigo-50 border-indigo-500 ring-1 ring-indigo-500' : 'border-gray-200'
                                        }`}
                                >
                                    <span className="font-bold mr-2 text-gray-400">{String.fromCharCode(65 + idx)}.</span>
                                    {opt}
                                </button>
                            ))}
                        </div>
                    )}

                    {type === 'mc' && (
                        <div className="space-y-2">
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
                                        className={`w-full text-left p-4 border rounded-lg hover:bg-gray-50 ${selected ? 'bg-indigo-50 border-indigo-500 ring-1 ring-indigo-500' : 'border-gray-200'
                                            }`}
                                    >
                                        <div className="flex items-center">
                                            <div className={`w-5 h-5 border rounded mr-3 flex items-center justify-center ${selected ? 'bg-indigo-600 border-indigo-600' : 'border-gray-300'}`}>
                                                {selected && <Check className="w-3 h-3 text-white" />}
                                            </div>
                                            {opt}
                                        </div>
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>

                <div className="mt-8 flex justify-end">
                    <button
                        onClick={handleNext}
                        disabled={currentAnswer === undefined && type !== 'mc'} // MC can have empty answer? Usually no but let's allow skipping if needed, or enforce.
                        className={`px-6 py-2 rounded-md font-medium flex items-center ${(currentAnswer !== undefined || (type === 'mc' && currentAnswer?.length > 0))
                                ? 'bg-indigo-600 text-white hover:bg-indigo-700'
                                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                            }`}
                    >
                        {currentIndex === questions.length - 1 ? 'Finalizar' : 'Siguiente'}
                        <ArrowRight className="ml-2 w-4 h-4" />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Quiz;

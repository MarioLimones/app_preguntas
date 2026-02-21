import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../core/autenticacion/AuthContext';
import { LogOut, Home, Historial, User, Users } from 'lucide-react';

const MainLayout = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
            <header className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex">
                            <Link to="/" className="shrink-0 flex items-center text-xl font-bold text-indigo-600">
                                PreguntasApp
                            </Link>
                            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                                <Link to="/" className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                    <Home className="w-4 h-4 mr-2" />
                                    Inicio
                                </Link>
                                <Link to="/historial" className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                    <Historial className="w-4 h-4 mr-2" />
                                    Historial
                                </Link>
                                {user?.role === 'ADMIN' && (
                                    <Link to="/users" className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                        <Users className="w-4 h-4 mr-2" />
                                        Usuarios
                                    </Link>
                                )}
                            </div>
                        </div>
                        <div className="flex items-center">
                            {user ? (
                                <div className="flex items-center space-x-4">
                                    <div className="hidden md:flex flex-col items-end">
                                        <span className="text-sm font-semibold text-gray-900 leading-none">
                                            {user.username}
                                        </span>
                                        <span className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-1">
                                            {user.role}
                                        </span>
                                    </div>
                                    <div className="w-8 h-8 rounded-full bg-indigo-50 flex items-center justify-center text-indigo-600 border border-indigo-100">
                                        <User className="w-4 h-4" />
                                    </div>
                                    <button
                                        onClick={handleLogout}
                                        className="text-gray-400 hover:text-red-500 transition-colors p-1"
                                        title="Cerrar Sesión"
                                    >
                                        <LogOut className="w-5 h-5" />
                                    </button>
                                </div>
                            ) : (
                                <div className="space-x-4">
                                    <Link to="/login" className="text-gray-500 hover:text-gray-900 font-medium text-sm">Login</Link>
                                    <Link to="/register" className="bg-indigo-600 text-white px-4 py-2 rounded-xl hover:bg-indigo-700 text-sm font-bold shadow-lg shadow-indigo-100 transition-all">Registro</Link>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </header>
            <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <Outlet />
            </main>
            <footer className="bg-white border-t border-gray-100 mt-auto">
                <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row justify-between items-center gap-4">
                    <p className="text-sm text-gray-400">&copy; 2026 PreguntasApp. Premium Preguntas Experience.</p>
                    <div className="flex gap-6">
                        <span className="text-xs text-gray-300 font-medium">Versión 2.0 (Optimized)</span>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default MainLayout;

import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './core/autenticacion/AuthContext';
import MainLayout from './layout/MainLayout';
import Login from './features/autenticacion/Login';
import Register from './features/autenticacion/Register';
import Panel from './features/panel/panel';
import Preguntas from './features/preguntas/preguntas';
import Historial from './features/historial/historial';
import GestionUsuarios from './features/administracion/GestionUsuarios';

const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return <div className="p-10 text-center font-medium text-gray-500">Cargando aplicación...</div>;
  if (!user) return <Navigate to="/login" replace />;
  return children;
};

const AdminRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return <div className="p-10 text-center font-medium text-gray-500">Verificando permisos...</div>;
  if (!user || user.role !== 'ADMIN') return <Navigate to="/" replace />;
  return children;
};

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route path="/" element={
        <ProtectedRoute>
          <MainLayout />
        </ProtectedRoute>
      }>
        <Route index element={<Panel />} />
        <Route path="Preguntas/:type" element={<Preguntas />} />
        <Route path="Historial" element={<Historial />} />
        <Route path="users" element={
          <AdminRoute>
            <GestionUsuarios />
          </AdminRoute>
        } />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;

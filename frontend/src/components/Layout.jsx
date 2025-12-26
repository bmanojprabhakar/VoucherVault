import React from 'react';
import { useAuth } from '../context/AuthContext';
import { LogOut, Ticket } from 'lucide-react';
import { Link, Outlet } from 'react-router-dom';

const Layout = () => {
    const { user, logout } = useAuth();

    return (
        <div className="min-h-screen bg-gray-950 text-gray-100 font-sans">
            <nav className="bg-gray-900 border-b border-gray-800">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        <Link to="/" className="flex items-center gap-2">
                            <div className="bg-indigo-600 p-2 rounded-lg">
                                <Ticket className="w-5 h-5 text-white" />
                            </div>
                            <span className="font-bold text-xl tracking-tight">VoucherVault</span>
                        </Link>

                        <div className="flex items-center gap-4">
                            <span className="text-sm text-gray-400 hidden sm:block">{user?.email}</span>
                            <button
                                onClick={logout}
                                className="p-2 hover:bg-gray-800 rounded-full transition-colors text-gray-400 hover:text-white"
                                title="Logout"
                            >
                                <LogOut className="w-5 h-5" />
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;

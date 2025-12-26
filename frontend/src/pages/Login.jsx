import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGoogleLogin } from '@react-oauth/google';
import { useAuth } from '../context/AuthContext';
import { Lock } from 'lucide-react';

const Login = () => {
    const { login, user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            navigate('/');
        }
    }, [user, navigate]);

    const googleLogin = useGoogleLogin({
        onSuccess: (codeResponse) => login(codeResponse.code),
        flow: 'auth-code',
        onError: (error) => console.log('Login Failed:', error),
    });

    return (
        <div className="min-h-screen bg-gray-900 flex items-center justify-center p-4">
            <div className="bg-gray-800 p-8 rounded-xl shadow-2xl w-full max-w-md border border-gray-700">
                <div className="flex flex-col items-center mb-8">
                    <div className="p-3 bg-indigo-600 rounded-full mb-4">
                        <Lock className="w-8 h-8 text-white" />
                    </div>
                    <h1 className="text-2xl md:text-3xl font-bold text-white mb-2">VoucherVault</h1>
                    <p className="text-gray-400 text-center">Securely manage your private coupons</p>
                </div>

                <button
                    onClick={() => googleLogin()}
                    className="w-full flex items-center justify-center gap-3 bg-white hover:bg-gray-100 text-gray-900 font-semibold py-3 px-6 rounded-lg transition-colors duration-200"
                >
                    <img src="https://www.google.com/favicon.ico" alt="Google" className="w-6 h-6" />
                    Sign in with Google
                </button>

                {import.meta.env.DEV && (
                    <button
                        onClick={async () => {
                            try {
                                const { data } = await import('../api').then(m => m.default.post('/auth/dev-login'));
                                localStorage.setItem('token', data.token);
                                window.location.href = '/';
                            } catch (e) {
                                console.error(e);
                                alert('Dev login failed');
                            }
                        }}
                        className="w-full mt-4 flex items-center justify-center gap-3 bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors duration-200"
                    >
                        <Lock className="w-5 h-5" />
                        Dev Profile Login (Bypass)
                    </button>
                )}
            </div>
        </div>
    );
};

export default Login;

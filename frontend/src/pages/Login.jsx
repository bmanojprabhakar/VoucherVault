import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGoogleLogin } from '@react-oauth/google';
import { useAuth } from '../context/AuthContext';
import { Lock, Loader2 } from 'lucide-react';

const Login = () => {
    const { login, user } = useAuth();
    const navigate = useNavigate();
    const [isAuthenticating, setIsAuthenticating] = useState(false);

    useEffect(() => {
        if (user) {
            navigate('/');
        }
    }, [user, navigate]);

    const handleGoogleLogin = useGoogleLogin({
        onSuccess: async (codeResponse) => {
            setIsAuthenticating(true);
            try {
                await login(codeResponse.code);
            } catch (error) {
                console.error('Login failed:', error);
                setIsAuthenticating(false);
            }
        },
        flow: 'auth-code',
        onError: (error) => {
            console.log('Login Failed:', error);
            setIsAuthenticating(false);
        },
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
                    onClick={() => {
                        setIsAuthenticating(true);
                        handleGoogleLogin();
                    }}
                    disabled={isAuthenticating}
                    className="w-full flex items-center justify-center gap-3 bg-white hover:bg-gray-100 disabled:bg-gray-200 disabled:cursor-not-allowed text-gray-900 font-semibold py-3 px-6 rounded-lg transition-all duration-200"
                >
                    {isAuthenticating ? (
                        <>
                            <Loader2 className="w-5 h-5 animate-spin" />
                            Signing in...
                        </>
                    ) : (
                        <>
                            <img src="https://www.google.com/favicon.ico" alt="Google" className="w-6 h-6" />
                            Sign in with Google
                        </>
                    )}
                </button>

                {import.meta.env.DEV && (
                    <button
                        onClick={async () => {
                            setIsAuthenticating(true);
                            try {
                                const { data } = await import('../api').then(m => m.default.post('/auth/dev-login'));
                                localStorage.setItem('token', data.token);
                                window.location.href = '/';
                            } catch (e) {
                                console.error(e);
                                setIsAuthenticating(false);
                                alert('Dev login failed');
                            }
                        }}
                        disabled={isAuthenticating}
                        className="w-full mt-4 flex items-center justify-center gap-3 bg-green-600 hover:bg-green-700 disabled:bg-green-800 disabled:cursor-not-allowed text-white font-semibold py-3 px-6 rounded-lg transition-colors duration-200"
                    >
                        {isAuthenticating ? (
                            <Loader2 className="w-5 h-5 animate-spin" />
                        ) : (
                            <Lock className="w-5 h-5" />
                        )}
                        Dev Profile Login (Bypass)
                    </button>
                )}
            </div>
        </div>
    );
};

export default Login;

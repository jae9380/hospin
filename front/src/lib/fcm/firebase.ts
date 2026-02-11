// Import the functions you need from the SDKs you need
import { initializeApp, getApps, getApp } from 'firebase/app';
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
	apiKey: import.meta.env.FIREBASE_API_KEY_ID,
	authDomain: 'hospin-57573.firebaseapp.com',
	projectId: 'hospin-57573',
	storageBucket: 'hospin-57573.firebasestorage.app',
	messagingSenderId: '732747242912',
	appId: '1:732747242912:web:b02467bafaa64e95b858c5'
};

// Initialize Firebase
export const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApp();

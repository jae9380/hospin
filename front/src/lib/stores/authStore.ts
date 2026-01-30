import { writable } from 'svelte/store';

export const authStore = writable<{ isLoggedIn: boolean }>({
	isLoggedIn: false
});

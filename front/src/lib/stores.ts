import { writable } from 'svelte/store';
import type { RecommendedSpecialty } from '$lib/types/symptomcheck/recommendedSpecialty';

export const lastResult = writable<any>(null);

export const lastSpecialties = writable<RecommendedSpecialty[]>([]);

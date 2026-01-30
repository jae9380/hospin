import { writable } from 'svelte/store';
import type { RecommendedSpecialty } from '$lib/types/apiResponse/symptomcheck/recommendedSpecialty';

export const lastResult = writable<any>(null);

export const lastSpecialties = writable<RecommendedSpecialty[]>([]);

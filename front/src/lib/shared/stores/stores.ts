import { writable } from 'svelte/store';
import type { RecommendedSpecialty } from '$lib/shared/types/symptomcheck';

export const lastResult = writable<any>(null);

export const lastSpecialties = writable<RecommendedSpecialty[]>([]);

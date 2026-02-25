import type { Hospital } from '$lib/types/hospital/hospital';
export type RecommendedSpecialty = {
	name: string;
	reasons: string[];
	hospitals: Hospital[];
};

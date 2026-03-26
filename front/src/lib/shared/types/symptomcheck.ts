import type { Hospital } from '$lib/shared/types/hospital';

export type RecommendedSpecialty = {
	name: string;
	reasons: string[];
	hospitals: Hospital[];
};

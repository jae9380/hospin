import type { Hospital } from '$lib/types/apiResponse/symptomcheck/hospital';
export type RecommendedSpecialty = {
	name: string;
	reasons: string[];
	hospitals: Hospital[];
};

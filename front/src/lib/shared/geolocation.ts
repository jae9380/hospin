export interface GeoPosition {
	lat: number;
	lng: number;
}

export interface GeoResult {
	position: GeoPosition | null;
	errorMessage: string | null;
}

export async function getCurrentPosition(): Promise<GeoResult> {
	if (!navigator.geolocation) {
		return { position: null, errorMessage: '이 브라우저는 위치 정보를 지원하지 않습니다.' };
	}

	return new Promise<GeoResult>((resolve) => {
		navigator.geolocation.getCurrentPosition(
			(pos) => resolve({ position: { lat: pos.coords.latitude, lng: pos.coords.longitude }, errorMessage: null }),
			(err) => resolve({ position: null, errorMessage: '위치 정보를 가져오는데 실패했습니다: ' + err.message })
		);
	});
}

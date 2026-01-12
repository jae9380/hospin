// src/lib/naverMapLoader.ts
export function loadNaverMapScript() {
  return new Promise((resolve, reject) => {
    // 이미 naver 객체가 있으면 다시 안 불러옴
    if (window.naver && window.naver.maps) {
      resolve(true);
      return;
    }

    const script = document.createElement('script');
    script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${import.meta.env.VITE_NCP_KEY_ID}`;
    script.async = true;
    script.onload = resolve;
    script.onerror = reject;
    document.head.appendChild(script);
  });
}
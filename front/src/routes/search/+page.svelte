<script lang="ts">
	let selectedRegion = '';
	let selectedDistrict = '';
    let postalCode = '';

    let results: HospitalListResponse[] = [];

	// 임시 작성
	let name = '';
	let address = '';
	let callNumber = '';
	let callNumberError = '';
	let categoryCode = '';
	let page = 0;
	let totalPages = 0;

	// 시도 코드
	const regionOptions = [
		{ code: '110000', label: '서울' },
		{ code: '210000', label: '부산' },
		{ code: '220000', label: '인천' },
		{ code: '230000', label: '대구' },
		{ code: '240000', label: '광주' },
		{ code: '250000', label: '대전' },
		{ code: '260000', label: '울산' },
		{ code: '310000', label: '경기' },
		{ code: '320000', label: '강원' },
		{ code: '330000', label: '충북' },
		{ code: '340000', label: '충남' },
		{ code: '350000', label: '전북' },
		{ code: '360000', label: '전남' },
		{ code: '370000', label: '경북' },
		{ code: '380000', label: '경남' },
		{ code: '390000', label: '제주' },
		{ code: '410000', label: '세종' }
	];

	// 시군구 코드
	const allDistrictOptions = [
		{ code: '110001', parent: '110000', label: '강남구' },
		{ code: '110002', parent: '110000', label: '강동구' },
		{ code: '110003', parent: '110000', label: '강서구' },
		{ code: '110004', parent: '110000', label: '관악구' },
		{ code: '110005', parent: '110000', label: '구로구' },
		{ code: '110006', parent: '110000', label: '도봉구' },
		{ code: '110007', parent: '110000', label: '동대문구' },
		{ code: '110008', parent: '110000', label: '동작구' },
		{ code: '110009', parent: '110000', label: '마포구' },
		{ code: '110010', parent: '110000', label: '서대문구' },
		{ code: '110011', parent: '110000', label: '성동구' },
		{ code: '110012', parent: '110000', label: '성북구' },
		{ code: '110013', parent: '110000', label: '영등포구' },
		{ code: '110014', parent: '110000', label: '용산구' },
		{ code: '110015', parent: '110000', label: '은평구' },
		{ code: '110016', parent: '110000', label: '종로구' },
		{ code: '110017', parent: '110000', label: '중구' },
		{ code: '110018', parent: '110000', label: '송파구' },
		{ code: '110019', parent: '110000', label: '중랑구' },
		{ code: '110020', parent: '110000', label: '양천구' },
		{ code: '110021', parent: '110000', label: '서초구' },
		{ code: '110022', parent: '110000', label: '노원구' },
		{ code: '110023', parent: '110000', label: '광진구' },
		{ code: '110024', parent: '110000', label: '강북구' },
		{ code: '110025', parent: '110000', label: '금천구' },

		{ code: '210001', parent: '210000', label: '부산남구' },
		{ code: '210002', parent: '210000', label: '부산동구' },
		{ code: '210003', parent: '210000', label: '부산동래구' },
		{ code: '210004', parent: '210000', label: '부산진구' },
		{ code: '210005', parent: '210000', label: '부산북구' },
		{ code: '210006', parent: '210000', label: '부산서구' },
		{ code: '210007', parent: '210000', label: '부산영도구' },
		{ code: '210008', parent: '210000', label: '부산중구' },
		{ code: '210009', parent: '210000', label: '부산해운대구' },
		{ code: '210010', parent: '210000', label: '부산사하구' },
		{ code: '210011', parent: '210000', label: '부산금정구' },
		{ code: '210012', parent: '210000', label: '부산강서구' },
		{ code: '210013', parent: '210000', label: '부산연제구' },
		{ code: '210014', parent: '210000', label: '부산수영구' },
		{ code: '210015', parent: '210000', label: '부산사상구' },
		{ code: '210100', parent: '210000', label: '부산기장군' },

		{ code: '220001', parent: '220000', label: '인천미추홀구' },
		{ code: '220002', parent: '220000', label: '인천동구' },
		{ code: '220003', parent: '220000', label: '인천부평구' },
		{ code: '220004', parent: '220000', label: '인천중구' },
		{ code: '220005', parent: '220000', label: '인천서구' },
		{ code: '220006', parent: '220000', label: '인천남동구' },
		{ code: '220007', parent: '220000', label: '인천연수구' },
		{ code: '220008', parent: '220000', label: '인천계양구' },
		{ code: '220100', parent: '220000', label: '인천강화군' },
		{ code: '220200', parent: '220000', label: '인천옹진군' },

		{ code: '230001', parent: '230000', label: '대구남구' },
		{ code: '230002', parent: '230000', label: '대구동구' },
		{ code: '230003', parent: '230000', label: '대구북구' },
		{ code: '230004', parent: '230000', label: '대구서구' },
		{ code: '230005', parent: '230000', label: '대구수성구' },
		{ code: '230006', parent: '230000', label: '대구중구' },
		{ code: '230007', parent: '230000', label: '대구달서구' },
		{ code: '230100', parent: '230000', label: '대구달성군' },
		{ code: '230200', parent: '230000', label: '대구군위군' },

		{ code: '240001', parent: '240000', label: '광주동구' },
		{ code: '240002', parent: '240000', label: '광주북구' },
		{ code: '240003', parent: '240000', label: '광주서구' },
		{ code: '240004', parent: '240000', label: '광주광산구' },
		{ code: '240005', parent: '240000', label: '광주남구' },

		{ code: '250001', parent: '250000', label: '대전유성구' },
		{ code: '250002', parent: '250000', label: '대전대덕구' },
		{ code: '250003', parent: '250000', label: '대전서구' },
		{ code: '250004', parent: '250000', label: '대전동구' },
		{ code: '250005', parent: '250000', label: '대전중구' },

		{ code: '260001', parent: '260000', label: '울산남구' },
		{ code: '260002', parent: '260000', label: '울산동구' },
		{ code: '260003', parent: '260000', label: '울산중구' },
		{ code: '260004', parent: '260000', label: '울산북구' },
		{ code: '260100', parent: '260000', label: '울산울주군' },

		{ code: '310001', parent: '310000', label: '가평군' },
		{ code: '310002', parent: '310000', label: '강화군' },
		{ code: '310006', parent: '310000', label: '시흥시' },
		{ code: '310008', parent: '310000', label: '양주군' },
		{ code: '310009', parent: '310000', label: '양평군' },
		{ code: '310010', parent: '310000', label: '여주군' },
		{ code: '310011', parent: '310000', label: '연천군' },
		{ code: '310012', parent: '310000', label: '옹진군' },
		{ code: '310016', parent: '310000', label: '평택군' },
		{ code: '310017', parent: '310000', label: '포천군' },
		{ code: '310100', parent: '310000', label: '광명시' },
		{ code: '310200', parent: '310000', label: '동두천시' },
		{ code: '310300', parent: '310000', label: '부천시' },
		{ code: '310301', parent: '310000', label: '부천소사구' },
		{ code: '310302', parent: '310000', label: '부천오정구' },
		{ code: '310303', parent: '310000', label: '부천원미구' },
		{ code: '310401', parent: '310000', label: '성남수정구' },
		{ code: '310402', parent: '310000', label: '성남중원구' },
		{ code: '310403', parent: '310000', label: '성남분당구' },
		{ code: '310500', parent: '310000', label: '송탄시' },
		{ code: '310600', parent: '310000', label: '수원시' },
		{ code: '310601', parent: '310000', label: '수원권선구' },
		{ code: '310602', parent: '310000', label: '수원장안구' },
		{ code: '310603', parent: '310000', label: '수원팔달구' },
		{ code: '310604', parent: '310000', label: '수원영통구' },
		{ code: '310701', parent: '310000', label: '안양만안구' },
		{ code: '310702', parent: '310000', label: '안양동안구' },
		{ code: '310800', parent: '310000', label: '의정부시' },
		{ code: '310900', parent: '310000', label: '과천시' },
		{ code: '311000', parent: '310000', label: '구리시' },
		{ code: '311100', parent: '310000', label: '안산시' },
		{ code: '311101', parent: '310000', label: '안산단원구' },
		{ code: '311102', parent: '310000', label: '안산상록구' },
		{ code: '311200', parent: '310000', label: '평택시' },
		{ code: '311300', parent: '310000', label: '하남시' },
		{ code: '311400', parent: '310000', label: '군포시' },
		{ code: '311500', parent: '310000', label: '남양주시' },
		{ code: '311600', parent: '310000', label: '의왕시' },
		{ code: '311700', parent: '310000', label: '시흥시' },
		{ code: '311800', parent: '310000', label: '오산시' },
		{ code: '311901', parent: '310000', label: '고양덕양구' },
		{ code: '311902', parent: '310000', label: '고양일산서구' },
		{ code: '311903', parent: '310000', label: '고양일산동구' },
		{ code: '312000', parent: '310000', label: '용인시' },
		{ code: '312001', parent: '310000', label: '용인기흥구' },
		{ code: '312002', parent: '310000', label: '용인수지구' },
		{ code: '312003', parent: '310000', label: '용인처인구' },
		{ code: '312100', parent: '310000', label: '이천시' },
		{ code: '312200', parent: '310000', label: '파주시' },
		{ code: '312300', parent: '310000', label: '김포시' },
		{ code: '312400', parent: '310000', label: '안성시' },
		{ code: '312500', parent: '310000', label: '화성시' },
		{ code: '312600', parent: '310000', label: '광주시' },
		{ code: '312700', parent: '310000', label: '양주시' },
		{ code: '312800', parent: '310000', label: '포천시' },
		{ code: '312900', parent: '310000', label: '여주시' },

		{ code: '320001', parent: '320000', label: '고성군' },
		{ code: '320004', parent: '320000', label: '양구군' },
		{ code: '320005', parent: '320000', label: '양양군' },
		{ code: '320006', parent: '320000', label: '영월군' },
		{ code: '320008', parent: '320000', label: '인제군' },
		{ code: '320009', parent: '320000', label: '정선군' },
		{ code: '320010', parent: '320000', label: '철원군' },
		{ code: '320012', parent: '320000', label: '평창군' },
		{ code: '320013', parent: '320000', label: '홍천군' },
		{ code: '320014', parent: '320000', label: '화천군' },
		{ code: '320015', parent: '320000', label: '횡성군' },
		{ code: '320100', parent: '320000', label: '강릉시' },
		{ code: '320200', parent: '320000', label: '동해시' },
		{ code: '320300', parent: '320000', label: '속초시' },
		{ code: '320400', parent: '320000', label: '원주시' },
		{ code: '320500', parent: '320000', label: '춘천시' },
		{ code: '320600', parent: '320000', label: '태백시' },
		{ code: '320700', parent: '320000', label: '삼척시' },

		{ code: '330001', parent: '330000', label: '괴산군' },
		{ code: '330002', parent: '330000', label: '단양군' },
		{ code: '330003', parent: '330000', label: '보은군' },
		{ code: '330004', parent: '330000', label: '영동군' },
		{ code: '330005', parent: '330000', label: '옥천군' },
		{ code: '330006', parent: '330000', label: '음성군' },
		{ code: '330007', parent: '330000', label: '제천군' },
		{ code: '330009', parent: '330000', label: '진천군' },
		{ code: '330010', parent: '330000', label: '청원군' },
		{ code: '330011', parent: '330000', label: '증평군' },
		{ code: '330100', parent: '330000', label: '청주시' },
		{ code: '330101', parent: '330100', label: '청주상당구' },
		{ code: '330102', parent: '330100', label: '청주흥덕구' },
		{ code: '330103', parent: '330100', label: '청주청원구' },
		{ code: '330104', parent: '330100', label: '청주서원구' },
		{ code: '330200', parent: '330000', label: '충주시' },
		{ code: '330300', parent: '330000', label: '제천시' },

		{ code: '340002', parent: '340000', label: '금산군' },
		{ code: '340004', parent: '340000', label: '당진군' },
		{ code: '340007', parent: '340000', label: '부여군' },
		{ code: '340009', parent: '340000', label: '서천군' },
		{ code: '340011', parent: '340000', label: '연기군' },
		{ code: '340012', parent: '340000', label: '예산군' },
		{ code: '340013', parent: '340000', label: '천안군' },
		{ code: '340014', parent: '340000', label: '청양군' },
		{ code: '340015', parent: '340000', label: '홍성군' },
		{ code: '340016', parent: '340000', label: '태안군' },
		{ code: '340200', parent: '340000', label: '천안시' },
		{ code: '340201', parent: '340200', label: '천안서북구' },
		{ code: '340202', parent: '340200', label: '천안동남구' },
		{ code: '340300', parent: '340000', label: '공주시' },
		{ code: '340400', parent: '340000', label: '보령시' },
		{ code: '340500', parent: '340000', label: '아산시' },
		{ code: '340600', parent: '340000', label: '서산시' },
		{ code: '340700', parent: '340000', label: '논산시' },
		{ code: '340800', parent: '340000', label: '계룡시' },
		{ code: '340900', parent: '340000', label: '당진시' },

		{ code: '350001', parent: '350000', label: '고창군' },
		{ code: '350004', parent: '350000', label: '무주군' },
		{ code: '350005', parent: '350000', label: '부여군' },
		{ code: '350006', parent: '350000', label: '순창군' },
		{ code: '350008', parent: '350000', label: '완주군' },
		{ code: '350009', parent: '350000', label: '익산군' },
		{ code: '350010', parent: '350000', label: '임실군' },
		{ code: '350011', parent: '350000', label: '장수군' },
		{ code: '350013', parent: '350000', label: '진안군' },
		{ code: '350100', parent: '350000', label: '군산시' },
		{ code: '350200', parent: '350000', label: '남원시' },
		{ code: '350300', parent: '350000', label: '익산시' },
		{ code: '350400', parent: '350000', label: '전주시' },
		{ code: '350401', parent: '350400', label: '전주완산구' },
		{ code: '350402', parent: '350400', label: '전주덕진구' },
		{ code: '350500', parent: '350000', label: '정읍시' },
		{ code: '350600', parent: '350000', label: '김제시' },

		{ code: '360001', parent: '360000', label: '강진군' },
		{ code: '360002', parent: '360000', label: '고흥군' },
		{ code: '360003', parent: '360000', label: '곡성군' },
		{ code: '360006', parent: '360000', label: '구례군' },
		{ code: '360008', parent: '360000', label: '담양군' },
		{ code: '360009', parent: '360000', label: '무안군' },
		{ code: '360010', parent: '360000', label: '보성군' },
		{ code: '360012', parent: '360000', label: '신안군' },
		{ code: '360014', parent: '360000', label: '영광군' },
		{ code: '360015', parent: '360000', label: '영암군' },
		{ code: '360016', parent: '360000', label: '완도군' },
		{ code: '360017', parent: '360000', label: '장성군' },
		{ code: '360018', parent: '360000', label: '장흥군' },
		{ code: '360019', parent: '360000', label: '진도군' },
		{ code: '360020', parent: '360000', label: '함평군' },
		{ code: '360021', parent: '360000', label: '해남군' },
		{ code: '360022', parent: '360000', label: '화순군' },
		{ code: '360200', parent: '360000', label: '나주시' },
		{ code: '360300', parent: '360000', label: '목포시' },
		{ code: '360400', parent: '360000', label: '순천시' },
		{ code: '360500', parent: '360000', label: '여수시' },
		{ code: '360700', parent: '360000', label: '광양시' },

		{ code: '370002', parent: '370000', label: '고령군' },
		{ code: '370003', parent: '370000', label: '군위군' },
		{ code: '370005', parent: '370000', label: '달성군' },
		{ code: '370007', parent: '370000', label: '봉화군' },
		{ code: '370010', parent: '370000', label: '성주군' },
		{ code: '370012', parent: '370000', label: '영덕군' },
		{ code: '370013', parent: '370000', label: '영양군' },
		{ code: '370014', parent: '370000', label: '영일군' },
		{ code: '370017', parent: '370000', label: '예천군' },
		{ code: '370018', parent: '370000', label: '울릉군' },
		{ code: '370019', parent: '370000', label: '울진군' },
		{ code: '370021', parent: '370000', label: '의성군' },
		{ code: '370022', parent: '370000', label: '청도군' },
		{ code: '370023', parent: '370000', label: '청송군' },
		{ code: '370024', parent: '370000', label: '칠곡군' },
		{ code: '370100', parent: '370000', label: '경주시' },
		{ code: '370200', parent: '370000', label: '구미시' },
		{ code: '370300', parent: '370000', label: '김천시' },
		{ code: '370400', parent: '370000', label: '안동시' },
		{ code: '370500', parent: '370000', label: '영주시' },
		{ code: '370600', parent: '370000', label: '영천시' },
		{ code: '370700', parent: '370000', label: '포항시' },
		{ code: '370701', parent: '370700', label: '포항남구' },
		{ code: '370702', parent: '370700', label: '포항북구' },
		{ code: '370800', parent: '370000', label: '문경시' },
		{ code: '370900', parent: '370000', label: '상주시' },
		{ code: '371000', parent: '370000', label: '경산시' },

		{ code: '380002', parent: '380000', label: '거창군' },
		{ code: '380003', parent: '380000', label: '고성군' },
		{ code: '380004', parent: '380000', label: '김해군' },
		{ code: '380005', parent: '380000', label: '남해군' },
		{ code: '380007', parent: '380000', label: '사천군' },
		{ code: '380008', parent: '380000', label: '산청군' },
		{ code: '380011', parent: '380000', label: '의령군' },
		{ code: '380012', parent: '380000', label: '창원군' },
		{ code: '380014', parent: '380000', label: '창녕군' },
		{ code: '380016', parent: '380000', label: '하동군' },
		{ code: '380017', parent: '380000', label: '함안군' },
		{ code: '380018', parent: '380000', label: '함양군' },
		{ code: '380019', parent: '380000', label: '합천군' },
		{ code: '380100', parent: '380000', label: '김포시' },
		{ code: '380200', parent: '380000', label: '마산시' },
		{ code: '380300', parent: '380000', label: '사천시' },
		{ code: '380500', parent: '380000', label: '진주시' },
		{ code: '380600', parent: '380000', label: '진해시' },
		{ code: '380700', parent: '380000', label: '창원시' },
		{ code: '380701', parent: '380700', label: '창원마산회원구' },
		{ code: '380702', parent: '380700', label: '창원마산합포구' },
		{ code: '380703', parent: '380700', label: '창원진해구' },
		{ code: '380704', parent: '380700', label: '창원의창구' },
		{ code: '380705', parent: '380700', label: '창원성산구' },
		{ code: '380800', parent: '380000', label: '통영시' },
		{ code: '380900', parent: '380000', label: '밀양시' },
		{ code: '381000', parent: '380000', label: '거제시' },
		{ code: '381100', parent: '380000', label: '양산시' },

		{ code: '390001', parent: '390000', label: '남제주군' },
		{ code: '390002', parent: '390000', label: '북제주군' },
		{ code: '390100', parent: '390000', label: '서귀포시' },
		{ code: '390200', parent: '390000', label: '제주시' },

		{ code: '410000', parent: '410000', label: '세종시' }
	];

	// 선택된 시도 코드에 따라 districtOptions 자동 필터링
	$: districtOptions = allDistrictOptions.filter((d) => d.parent === selectedRegion);

	// 종별 코드
	const categoryOptions = [
		{ code: '01', label: '상급종합병원' },
		{ code: '11', label: '종합병원' },
		{ code: '21', label: '병원' },
		{ code: '28', label: '요양병원' },
		{ code: '29', label: '정신병원' },
		{ code: '31', label: '의원' },
		{ code: '41', label: '치과병원' },
		{ code: '51', label: '치과의원' },
		{ code: '61', label: '조산원' },
		{ code: '71', label: '보건소' },
		{ code: '72', label: '보건지소' },
		{ code: '73', label: '보건진료소' },
		{ code: '74', label: '모자보건센타' },
		{ code: '75', label: '보건의료원' },
		{ code: '81', label: '약국' },
		{ code: '91', label: '한방종합병원' },
		{ code: '92', label: '한방병원' },
		{ code: '93', label: '한의원' },
		{ code: '94', label: '한약방' },
		{ code: 'AA', label: '병의원' }
	];

	async function handleSearch(targetPage: number, pageSize: number = 10) {
		page = targetPage;

		const params = new URLSearchParams({
			name: name || '',
			categoryCode: categoryCode?.toString() || '',
			regionCode: selectedRegion?.toString() || '',
			districtCode: selectedDistrict?.toString() || '',
			postalCode: postalCode?.toString() || '',
			address: address || '',
			page: page.toString(),
			size: pageSize.toString()
		});

		try {
			const response = await fetch(
				`http://localhost:8080/api/hospital/search?${params.toString()}`,
				{
					method: 'GET',
					headers: {
						'Content-Type': 'application/json'
					}
				}
			);

			if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

			const data: { content: HospitalListResponse[]; totalPages: number; totalElements: number } =
				await response.json();

			console.log('검색 결과:', data);
            console.log(params.toString);

			// 화면 상태 업데이트
			results = data.content;
			totalPages = data.totalPages;
		} catch (error) {
			console.error('검색 실패:', error);
            console.log(params.toString);
		}
	}
</script>

<svelte:head>
	<title>병원 검색</title>
</svelte:head>

<div class="flex min-h-screen flex-col items-center bg-base-200 px-4 py-12">
	<div class="w-full max-w-3xl space-y-4 rounded-xl bg-white p-6 shadow-lg">
		<h2 class="text-center text-2xl font-bold">🔍 병원 검색</h2>

		<!-- 병원 이름 -->
		<input
			type="text"
			placeholder="병원 이름"
			bind:value={name}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
		/>

		<!-- 시도 -->
		<select
			bind:value={selectedRegion}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
		>
			<option value="">- 지역 선택 -</option>
			{#each regionOptions as opt (opt.code)}
				<option value={opt.code}>{opt.label}</option>
			{/each}
		</select>

		<!-- 시군구 -->
		<select
			bind:value={selectedDistrict}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
			disabled={!selectedRegion}
		>
			<option value="">- 상세 지역 선택 -</option>
			{#each districtOptions as opt (opt.code)}
				<option value={opt.code}>{opt.label}</option>
			{/each}
		</select>

		<!-- 병원 카테고리 -->
		<select
			bind:value={categoryCode}
			class="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
		>
			<option value="">- 의료기관 분류 -</option>
			{#each categoryOptions as opt (opt.code)}
				<option value={opt.code}>{opt.label}</option>
			{/each}
		</select>

		<!-- 검색 버튼 -->
		<button
			on:click={() => handleSearch(0)}
			class="w-full rounded bg-blue-600 py-2 text-white transition hover:bg-blue-700"
		>
			검색
		</button>
	</div>

	<!-- 검색 결과 -->
	<div class="mt-8 w-full max-w-3xl space-y-4">
{#if results.length === 0}
	<p class="text-gray-500">검색 결과가 없습니다.</p>
{:else}
	{#each results as hospital (hospital.hospital_code)}
		<div
			class="cursor-pointer rounded-lg border bg-white p-4 shadow-sm hover:shadow-md transition"
			on:click={() => console.log('선택한 병원:', hospital)}
		>
			<p class="text-lg font-semibold">{hospital.name}</p>
			<p class="text-sm text-gray-600">{hospital.address}</p>
			<p class="text-sm text-gray-600">{hospital.callNumber ?? ''}</p>
		</div>
	{/each}
{/if}
		<!-- 페이지네이션 -->
		{#if totalPages > 0}
			<div class="mt-4 flex items-center justify-center gap-4">
				<button
					on:click={() => handleSearch(page - 1)}
					disabled={page <= 0}
					class="rounded bg-gray-200 px-4 py-1 hover:bg-gray-300 disabled:opacity-50"
				>
					◀ 이전
				</button>
				<span>
					페이지 {page + 1} / {totalPages}
				</span>
				<button
					on:click={() => handleSearch(page + 1)}
					disabled={page + 1 >= totalPages}
					class="rounded bg-gray-200 px-4 py-1 hover:bg-gray-300 disabled:opacity-50"
				>
					다음 ▶
				</button>
			</div>
		{/if}
	</div>
</div>

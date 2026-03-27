#!/usr/bin/env python3

import os
import requests
import subprocess
import time
from typing import Dict, Optional

class ServiceManager:
    """
    8080 포트로 들어온 요청을 socat을 통해 내부의 Docker 컨테이너 (8081 or 8082)로 전달.
    컨테이너를 번갈아 실행하며 새로운 버전을 띄우고, 준비가 완료되면 트래픽을 새 컨테이너로 전환함.
    """

    def __init__(self, socat_port: int = 8080, sleep_duration: int = 3) -> None:
        self.socat_port: int = socat_port
        self.sleep_duration: int = sleep_duration
        self.services: Dict[str, int] = {
            'hospin_1': 8081,
            'hospin_2': 8082
        }
        self.current_name: Optional[str] = None
        self.current_port: Optional[int] = None
        self.next_name: Optional[str] = None
        self.next_port: Optional[int] = None

        self.image_url: str = "ghcr.io/jae9380/hospin-back:latest"

    def _find_current_service(self) -> None:
        cmd = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $NF}}'"
        current_service = subprocess.getoutput(cmd)
        if not current_service:
            self.current_name, self.current_port = 'hospin_2', self.services['hospin_2']
        else:
            self.current_port = int(current_service.split(':')[-1])
            self.current_name = next((name for name, port in self.services.items() if port == self.current_port), None)

    def _find_next_service(self) -> None:
        self.next_name, self.next_port = next(
            ((name, port) for name, port in self.services.items() if name != self.current_name),
            (None, None)
        )

    def _remove_container(self, name: str) -> None:
        os.system(f"docker stop {name} 2> /dev/null")
        os.system(f"docker rm -f {name} 2> /dev/null")

    def _run_container(self, name: str, port: int) -> None:
        """
        환경변수는 서버의 /etc/hospin/.env 파일에서 읽어옴.
        필요한 환경변수 목록:
          DATABASE_PASSWORD  - MariaDB 비밀번호
          REDIS_PASSWORD     - Redis 비밀번호
          OPENAI_API_KEY     - OpenAI API 키
          MAIL_USERNAME      - Gmail 계정
          MAIL_PASSWORD      - Gmail 앱 비밀번호
          JWT_SECRET_KEY     - JWT 시크릿 키
        """
        os.system(
            f"docker run --name={name} "
            f"-p {port}:8080 "
            f"--restart unless-stopped "
            f"-e TZ=Asia/Seoul "
            f"-e DATABASE_PASSWORD=${{DATABASE_PASSWORD}} "
            f"-e REDIS_PASSWORD=${{REDIS_PASSWORD}} "
            f"-e OPENAI_API_KEY=${{OPENAI_API_KEY}} "
            f"-e MAIL_USERNAME=${{MAIL_USERNAME}} "
            f"-e MAIL_PASSWORD=${{MAIL_PASSWORD}} "
            f"-e JWT_SECRET_KEY=${{JWT_SECRET_KEY}} "
            f"--pull always "
            f"--network hospin-network "
            f"-d {self.image_url}"
        )

    def _switch_port(self) -> None:
        cmd = f"ps aux | grep 'socat -t0 TCP-LISTEN:{self.socat_port}' | grep -v grep | awk '{{print $2}}'"
        pid = subprocess.getoutput(cmd)

        if pid:
            os.system(f"kill -9 {pid} 2>/dev/null")

        subprocess.Popen(
            [
                "socat",
                f"TCP-LISTEN:{self.socat_port},fork,reuseaddr",
                f"TCP:localhost:{self.next_port}"
            ],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            preexec_fn=os.setpgrp  # ⭐ 핵심
        )

    def _is_service_ready(self, port: int) -> bool:
        """
        Spring Boot Actuator의 /actuator/health 엔드포인트로 준비 상태 확인
        SecurityConfig에서 /actuator/health는 인증 없이 접근 허용되어 있음
        """
        ready_url = f"http://127.0.0.1:{port}/actuator/health"
        for _ in range(12):  # 총 1분간 (5초 간격으로 12회 시도)
            try:
                response = requests.get(ready_url, timeout=5)
                print(f"Checking {ready_url} - Status code: {response.status_code}")
                if response.status_code == 200:
                    return True
            except requests.RequestException as e:
                print(f"Error checking service readiness on port {port}: {e}")
            time.sleep(5)
        return False

    def update_service(self) -> None:
        self._find_current_service()
        self._find_next_service()

        self._remove_container(self.next_name)
        self._run_container(self.next_name, self.next_port)

        while not self._is_service_ready(self.next_port):
            print(f"Waiting for {self.next_name} to be 'ready' on port {self.next_port}...")
            time.sleep(self.sleep_duration)

        print(f"{self.next_name} is now ready.")
        self._switch_port()

        if self.current_name is not None:
            self._remove_container(self.current_name)

        print("Switched service successfully!")

if __name__ == "__main__":
    manager = ServiceManager()
    manager.update_service()

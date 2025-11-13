Feature: Health Check
  Scenario: 시스템이 정상적인지 체크한다
    When 클라이언트가 API를 요청할 때
    Then 응답 상태 코드는 200 이어야 한다
    And 응답 본문은 "OK"이어야 한다

package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }


    /**
     * When 지하철 노선에서 기존 구간에서 중간에 새로운 구간을 등록을 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에서 기존 구간의 역 사이에 구간을 등록")
    @Test
    void AddLineSectionInMid() {
        //when
        Long 강남양재사이역 = 지하철역_생성_요청("강남양재사이역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강남양재사이역, 3));

        //Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_등록된_역을_순서대로_확인(response, 강남역, 강남양재사이역, 양재역);
    }

    /**
     * When 지하철 노선에서 기존 구간에서 새로운 상행종점 구간을 추가로 요청하면
     * Then 노선에 새로운 상행종점 구간이 추가된다.
     */
    @DisplayName("지하철 노선에서 기존 구간의 상행을 기준으로 새로운 상행종점 구간 등록")
    @Test
    void addLineSectionBaseOnUpStation() {
        // when
        Long 강남이전역 = 지하철역_생성_요청("강남이전역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남이전역, 강남역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_등록된_역을_순서대로_확인(response, 강남이전역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에서 기존 구간의 하행을 기준으로 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에서 기존 구간의 하행을 기준으로 구간을 등록")
    @Test
    void addLineSectionBaseOnDownStation() {
        // when
        Long 양재다음역 = 지하철역_생성_요청("양재다음역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재다음역, 7));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_등록된_역을_순서대로_확인(response, 강남역, 양재역, 양재다음역);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 구간을 추가할때, 기존 역 사이 길이보다 크거나 같으면
     * Then 노선에 구간을 추가할 수 없다
     */
    @DisplayName("추가할 구간이 기존 역 사이 길이보다 크거나 같으면 구간을 추가할 수 없다")
    @Test
    void tooLongSectionDistance() {
        //Given
        Long 거리가_긴_역 = 지하철역_생성_요청("거리가 긴 역").jsonPath().getLong("id");

        //When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(거리가_긴_역, 양재역, 15));

        //Then
        구간_추가에_실패(response);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 구간을 추가할때 지하철역이 이미 다 추가되어 있다면
     * Then 노선에 구간을 추가할 수 없다
     */
    @DisplayName("상행과 하행 이미 등록되어 있으면 구간을 추가할 수 없다.")
    @Test
    void alreadyhasBothStations() {
        //Given
        long 이미_있는_강남역 = 강남역;
        long 이미_있는_양재역 = 양재역;

        //When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(이미_있는_강남역, 이미_있는_양재역, 10));

        //Then
        구간_추가에_실패(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 구간을 추가할때 상행과 하행 둘다 포함되어 있지 않으면
     * Then 노선에 구간을 추가할 수 없다.
     */
    @DisplayName("상행과 하행 둘다 포함되어 있지 않으면 구간을 추가할 수 없다")
    @Test
    void noOneHasNotStations() {
        //Given
        Long 없는역 = 지하철역_생성_요청("없는역").jsonPath().getLong("id");
        Long 없는역2 = 지하철역_생성_요청("없는역2").jsonPath().getLong("id");


        //When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(없는역, 없는역2, 5));

        //Then
        구간_추가에_실패(response);

    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 역 제거를 요청 하면
     * Then 마지막 역이 삭제되고 노선이 변경된다.
     */
    @DisplayName("지하철 노선에 마지막 역을 제거")
    @Test
    void removeLastStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_등록된_역을_순서대로_확인(response, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫번째 역 제거를 요청 하면
     * Then 첫번째 역이 삭제되고 노선이 변경된다.
     */
    @DisplayName("지하철 노선에 첫번째 역을 제거")
    @Test
    void removeFrontStation() {
        // given
        Long 강남이전역 = 지하철역_생성_요청("강남이전역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남이전역, 강남역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남이전역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_등록된_역을_순서대로_확인(response, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간에 해당하는 역을 제거하면
     * Then 중간의 역이 삭제되고 노선이 변경된다.
     */
    @DisplayName("지하철 노선에 중간 역을 제거")
    @Test
    void removeMiddleStation() {
        // given
        Long 강남양재역 = 지하철역_생성_요청("강남양재역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강남양재역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_등록된_역을_순서대로_확인(response, 강남역, 양재역);

    }

    /**
     * When 지하철 구간이 하나 밖에 없는 상태에서 역을 제거하려고 하면
     * Then 삭제가 되지 않는다
     */

    @DisplayName("구간이 하나인 노선은 삭제할 수 없다")
    @Test
    void removeHasOneSectionLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        구간_삭제에_실패(response);
    }

    /**
     * Given 지하철 노선에 없는 새로운 구간 추가를 요청 하고
     * When 지하철 노선에 포함되어 있지 않은 역을 제거하려고 하면
     * Then 삭제가 되지 않는다
     */

    @DisplayName("구간에 없는 역은 삭제할 수 없다")
    @Test
    void removehasNotStation() {
        // given
        Long 마곡역 = 지하철역_생성_요청("마곡역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 마곡역);

        // then
        구간_삭제에_실패(response);
    }


    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}

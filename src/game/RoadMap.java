package game;

/**
 * Created by Gregory on 6/9/17.
 */
public class RoadMap {

    public static final int SECTION_SIZE = 360;
    public static final int NUM_CHUNKS = 7;
    public static final int NUM_ONEWAY_LANES = 3;
    public static final int LANE_WIDTH = SECTION_SIZE / (NUM_ONEWAY_LANES * 2);
    public static final int MAP_UNITS = RoadMap.SECTION_SIZE * 3 * RoadMap.NUM_CHUNKS;

    private static final SectionType[] indexToSection = {SectionType.BLOCKED, SectionType.ROAD, SectionType.BLOCKED, SectionType.ROAD, SectionType.INTERSECTION, SectionType.ROAD, SectionType.BLOCKED, SectionType.ROAD, SectionType.BLOCKED};

    public static SectionType getSectionTypeAt(float x, float y) {
        return indexToSection[(int) (Math.floorMod((int) y, (SECTION_SIZE * 3)) / SECTION_SIZE) * 3 + (int) (Math.floorMod((int) x, (SECTION_SIZE * 3)) / SECTION_SIZE)];
    }

    public static float lockToLane(int lane, float axis) {
        float sectionStart = axis - Math.floorMod((int) axis, RoadMap.SECTION_SIZE);
        if(lane < RoadMap.NUM_ONEWAY_LANES) return sectionStart + RoadMap.LANE_WIDTH * (lane + 0.5f);
        return sectionStart + RoadMap.SECTION_SIZE - RoadMap.LANE_WIDTH * (RoadMap.NUM_ONEWAY_LANES * 2 - 1 - lane +  0.5f);
    }

}

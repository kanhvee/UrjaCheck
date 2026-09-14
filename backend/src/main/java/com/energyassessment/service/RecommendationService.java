package com.energyassessment.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Simple, rule-based, non-judgmental recommendations. No unsafe electrical advice. */
@Service
public class RecommendationService {

    private static final Map<String, String> RECOMMENDATIONS = new HashMap<>();

    static {
        RECOMMENDATIONS.put("air conditioner",
                "Consider increasing the thermostat setting slightly where comfortable, cleaning filters regularly, " +
                        "and choosing an energy-efficient or inverter model when it's time to replace it.");
        RECOMMENDATIONS.put("air cooler",
                "Ensure adequate ventilation and clean cooling pads regularly for efficient operation.");
        RECOMMENDATIONS.put("ceiling fan",
                "Switch off fans in unoccupied rooms, and consider efficient BLDC fan models when replacing an old one.");
        RECOMMENDATIONS.put("table fan",
                "Switch off fans in unoccupied rooms.");
        RECOMMENDATIONS.put("lighting fixtures",
                "Consider energy-efficient LED lighting, switch off lights when not required, and make use of daylight where possible.");
        RECOMMENDATIONS.put("led bulb",
                "Switch off lights when a room is unoccupied, and make use of daylight where possible.");
        RECOMMENDATIONS.put("tube light",
                "Consider replacing older tube lights with LED alternatives for lower running consumption.");
        RECOMMENDATIONS.put("refrigerator",
                "Check door seals periodically, avoid keeping the door open longer than necessary, and maintain a suitable internal temperature setting.");
        RECOMMENDATIONS.put("water heater/geyser",
                "Reduce unnecessary continuous heating time and use an appropriate thermostat setting; a timer can help avoid unattended operation.");
        RECOMMENDATIONS.put("water pump",
                "Avoid unnecessary pumping cycles and check whether the pump runs longer than required for your storage capacity.");
        RECOMMENDATIONS.put("washing machine",
                "Running full loads instead of frequent small loads can reduce overall energy use.");
        RECOMMENDATIONS.put("television",
                "Switch off the television at the plug rather than leaving it on standby for long periods.");
        RECOMMENDATIONS.put("computer/laptop",
                "Enable power-saving/sleep settings and switch off the display when stepping away.");
        RECOMMENDATIONS.put("iron", "Iron larger batches of clothing together to avoid repeated heating cycles.");
        RECOMMENDATIONS.put("microwave", "Avoid unnecessarily long cook times and keep the interior clean for efficient heating.");
        RECOMMENDATIONS.put("induction cooktop", "Use cookware suited to induction heating for more efficient energy transfer.");
        RECOMMENDATIONS.put("mixer/grinder", "This appliance typically uses very little energy; no specific action needed.");
        RECOMMENDATIONS.put("electric kettle", "Boil only the amount of water you need for each use.");
        RECOMMENDATIONS.put("security system", "This appliance runs continuously by design; confirm its rated power matches the manufacturer specification.");
    }

    private static final String NEUTRAL_NOTE =
            "No specific recommendation — this appliance's estimated consumption is within or below the reference level.";

    private static final String GENERIC_ABOVE_NOTE =
            "Check whether this appliance's usage hours can be reduced without affecting comfort.";

    /**
     * @param applianceName the appliance's display name
     * @param status        BELOW_REFERENCE | WITHIN_REFERENCE_RANGE | ABOVE_REFERENCE
     */
    public String getRecommendation(String applianceName, String status) {
        if (!"ABOVE_REFERENCE".equals(status)) {
            return NEUTRAL_NOTE;
        }
        String key = applianceName == null ? "" : applianceName.toLowerCase(Locale.ROOT).trim();
        return RECOMMENDATIONS.getOrDefault(key, GENERIC_ABOVE_NOTE);
    }
}

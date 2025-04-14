
/*
 * A recommendation system where admins define rules per program to influence course scores for enrolled students.
 * 
 * Design Pattern -- 	Purpose
 * Strategy:	Handle multiple scoring strategies (e.g., keyword-based, tag-based)
 * Factory:	Dynamically select the right strategy for a rule type
 * Command (optional):	If you want each rule to be a reusable, pluggable "score instruction"
 * Builder (optional):	To build complex rules through admin UI or JSON
 */


src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── courserecommendation/
│   │               ├── config/
│   │               │   └── CorsConfig.java
│   │               │
│   │               ├── constant/
│   │               │   └── RecommendationConstants.java
│   │               │
│   │               ├── controller/
│   │               │   └── ProgramRecommendationController.java
│   │               │
│   │               ├── dto/
│   │               │   ├── ClassScheduleDTO.java
│   │               │   ├── CourseDTO.java
│   │               │   ├── CourseScoreDTO.java
│   │               │   └── ProgramDTO.java
│   │               │   └── ProgramRecommendationRuleDTO.java
│   │               │   └── RecommendationDTO.java
│   │               │
│   │               ├── model/
│   │               │   └── ProgramRecommendationRule.java
│   │               │   └── Recommendation.java
│   │               │
│   │               ├── repository/
│   │               │   └── ProgramRecommendationRuleRepository.java
│   │               │
│   │               ├── service/
│   │               │   ├── ProgramRecommendationService.java
│   │               │   └── registry/
│   │               │       └── ProgramRuleRegistry.java
│   │               │
│   │               ├── scoring/
│   │               │   ├── RecommendationRuleStrategy.java
│   │               │   ├── RecommendationScoringEngine.java
│   │               │   ├── factory/
│   │               │   │   └── RecommendationRuleStrategyFactory.java
│   │               │   └── strategy/
│   │               │       ├── TitleContainsStrategy.java
│   │               │       ├── LevelEqualsStrategy.java
│   │               │       └── CategoryEqualsStrategy.java
│   │               │
│   │               ├── webclient/
│   │               │   ├── CourseClient.java
│   │               │   ├── WebClientConfig.java
│   │               │
│   │               └── CourserecommendationApplication.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/
            └── example/
                └── courserecommendation/
                   └── ProgramRecommendationServiceTest.java
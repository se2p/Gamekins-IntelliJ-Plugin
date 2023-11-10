package org.plugin.plugin

// Constants.kt
object Constants {

    const val TEST_JOB = "test1"
    const val CHALLENGE_PANEL_DESCRIPTION = "Click on a challenge to expand and see more details if available"


    const val API_BASE_URL = "http://localhost:8080/jenkins/gamekins"
    const val TIMEOUT = 30
    val MAX_RETRIES = 3


    // CHALLENGES
    const val STORE_CHALLENGE = "/storeChallenge"
    const val GET_STORED_CHALLENGES = "/getStoredChallenges"
    const val GET_CURRENT_CHALLENGES = "/getCurrentChallenges"
    const val GET_REJECTED_CHALLENGES = "/getRejectedChallenges"
    const val GET_COMPLETED_CHALLENGES = "/getCompletedChallenges"

    //LEADERBOARD
    const val GET_USERS = "/getUsers"
    const val GET_TEAMS = "/getTeams"

    //Statistics
    const val GET_STATISTICS = "/getStatistics"


    const val TEST_STRING = "<Statistics project=\"d0358b00-7be8-413f-a1a3-f61c4f202db5\">\n" +
            "    <Users count=\"1\">\n" +
            "        <User id=\"b1506d12-4457-429a-88eb-4524cc35b442\" project=\"d0358b00-7be8-413f-a1a3-f61c4f202db5\" score=\"0\">\n" +
            "            <CurrentChallenges count=\"4\">\n" +
            "                <DummyChallenge/>\n" +
            "                <DummyChallenge/>\n" +
            "                <DummyChallenge/>\n" +
            "                <TestChallenge created=\"1698324935024\" solved=\"0\" tests=\"307\" testsAtSolved=\"0\"/>\n" +
            "            </CurrentChallenges>\n" +
            "            <CompletedChallenges count=\"0\">\n" +
            "            </CompletedChallenges>\n" +
            "            <RejectedChallenges count=\"0\">\n" +
            "            </RejectedChallenges>\n" +
            "            <StoredChallenges count=\"2\">\n" +
            "                <TestChallenge created=\"1698242129074\" solved=\"0\" tests=\"307\" testsAtSolved=\"0\"/>\n" +
            "                <TestChallenge created=\"1698313319276\" solved=\"0\" tests=\"307\" testsAtSolved=\"0\"/>\n" +
            "            </StoredChallenges>\n" +
            "            <CurrentQuests count=\"0\">\n" +
            "            </CurrentQuests>\n" +
            "            <CompletedQuests count=\"0\">\n" +
            "            </CompletedQuests>\n" +
            "            <RejectedQuests count=\"0\">\n" +
            "            </RejectedQuests>\n" +
            "            <UnfinishedQuests count=\"0\">\n" +
            "            </UnfinishedQuests>\n" +
            "            <CurrentQuestTasks count=\"0\">\n" +
            "            </CurrentQuestTasks>\n" +
            "            <CompletedQuestTasks count=\"0\">\n" +
            "            </CompletedQuestTasks>\n" +
            "            <Achievements count=\"0\">\n" +
            "            </Achievements>\n" +
            "        </User>\n" +
            "    </Users>\n" +
            "    <Runs count=\"0\">\n" +
            "    </Runs>\n" +
            "</Statistics>";

    const val HELP = " Help\n" +
            "The current challenges table displays the current Challenges you can solve\n" +
            "All necessary information about the Challenge like class, package or branch can be found in the description of the Challenge\n" +
            "There are seven types of Challenges:\n" +
            "Build Challenge: Generated after a build failed because of your commit. Fix the build to solve the Challenge\n" +
            "Test Challenge: Just write a new test without specific requirements\n" +
            "Class Coverage Challenge: Cover more lines in a specific class. It does not matter which lines\n" +
            "Method Coverage Challenge: Cover more lines in a specific method. It does not matter which lines\n" +
            "Line Coverage Challenge: Cover the specified line or at least one more branch if available\n" +
            "Mutation Test Challenge: Write a test or improve an existing one to kill the generated mutant.\n" +
            "Smell Challenge: Remove a code or test smell.\n" +
            "Click on a Challenge to get the content and more details,\n" +
            "The branch information states in which branch the Challenges was generated\n" +
            "Challenges can also be solved in other branches if the mentioned code is available\n" +
            "Challenges are only generated and solved during the run/build of a project/job in Jenkins\n" +
            "Normally, the build is triggered after each commit or after a specific amount of time. If you don't want to wait, you can start the build manually by clicking Build Now in the left panel\n" +
            "If the Challenge cannot be solved for some reason or the class/method under test should not be tested, the Challenges can be rejected\n" +
            "To reject a Challenge, click the Reject button on each Challenge and add a reason, why the Challenge was rejected\n" +
            "Challenges may be rejected automatically if they are not solvable anymore\n" +
            "Rejected Challenges won't appear to be generated a second time\n" +
            "After rejection a new Challenge is generated if the code of the project is available\n" +
            "Hover over a rejected Challenge to see the reason for rejection\n" +
            "If a Class Coverage Challenge is rejected, the whole class is not considered again for new Challenges\n" +
            "Rejected Class Coverage Challenges can be restored with the Undo-Button next to the Challenge\n" +
            "The Challenge will not be immediately active again, it has to be generated again to appear\n" +
            "To store a Challenge, click the store button\n" +
            "Stored Challenges cannot be completed and will count as rejected should they be solved\n" +
            "To view, unshelve and send stored Challenges press the Stored Challenges Button\n" +
            "Stored Challenges can be sent to another participant if that option is enabled\n" +
            "If there are no Challenges generated for you, although you have committed something recently, then check the Git Names in your profile settings\n" +
            "In addition to Challenges, Quests are generated and can be solved step by step, since they consist of different Challenges. The Challenges of a quest must be solved in the given order\n" +
            "Also have a look at the Achievements available in your profile. You can access them by clicking on the link of your name in the Leaderboard. Remember to choose the right project in the dropdown in your profile.\n" +
            "You can also view the Achievements of other players by clicking on their name in the leaderboard\n" +
            "The Avatar displayed in the leaderboard can be changed in your profile settings. There are 50 different avatars you can choose from\n" +
            "When a build for example solves a Challenge or generates a new one, notifications are sent to the given email address. You can disable this option in your profile settings"


}

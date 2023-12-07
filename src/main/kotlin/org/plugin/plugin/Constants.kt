package org.plugin.plugin

// Constants.kt
object Constants {

    const val CHALLENGE_PANEL_DESCRIPTION = "Click on a challenge to expand and see more details if available"


    const val API_BASE_URL = "http://localhost:8080/jenkins/gamekins"
    const val TOKEN_FILE_PATH = "./auth_token.txt"



    // CHALLENGES
    const val STORE_CHALLENGE = "/storeChallenge"
    const val STORE_CHALLENGE_LIMIT = "/getStoredChallengesLimit"
    const val RESTORE_CHALLENGE = "/restoreChallenge"
    const val UNSHELVE_CHALLENGE = "/unshelveChallenge"
    const val REJECT_CHALLENGE = "/rejectChallenge"
    const val GET_STORED_CHALLENGES = "/getStoredChallenges"
    const val GET_CURRENT_CHALLENGES = "/getCurrentChallenges"
    const val GET_REJECTED_CHALLENGES = "/getRejectedChallenges"
    const val GET_COMPLETED_CHALLENGES = "/getCompletedChallenges"


    // QUESTS
    const val GET_CURRENT_QUESTS_TASKS = "/getCurrentQuestTasks"
    const val GET_COMPLETED_QUESTS_TASKS = "/getCompletedQuestTasks"
    const val GET_CURRENT_QUESTS = "/getCurrentQuests"

    //LEADERBOARD
    const val GET_USERS = "/getUsers"
    const val GET_TEAMS = "/getTeams"

    //Achievements
    const val GET_COMPLETED_ACHIEVEMENTS = "/getCompletedAchievements"
    const val GET_UNSOLVED_ACHIEVEMENTS = "/getUnsolvedAchievements"

    //Statistics
    const val GET_STATISTICS = "/getStatistics"

    // Start service
    const val START_SOCKET = "/startSocket"

    const val HELP = "<h1 class=\"ml-2\">\n" +
            "        Help\n" +
            "    </h1>\n" +
            "    <ul>\n" +
            "        <li>The current challenges table displays the current Challenges you can solve</li>\n" +
            "        <li>All necessary information about the Challenge like <i>class</i>, <i>package</i> or <i>branch</i>\n" +
            "            can be found in the description of the Challenge\n" +
            "        </li>\n" +
            "        <li>There are seven types of Challenges:</li>\n" +
            "        <ol>\n" +
            "            <li><b>Build Challenge</b>: Generated after a build failed because of your commit. Fix the build\n" +
            "                to solve the Challenge\n" +
            "            </li>\n" +
            "            <li><b>Test Challenge</b>: Just write a new test without specific requirements\n" +
            "            </li>\n" +
            "            <li><b>Class Coverage Challenge</b>: Cover more lines in a specific class. It does not matter\n" +
            "                which lines\n" +
            "            </li>\n" +
            "            <li><b>Method Coverage Challenge</b>: Cover more lines in a specific method. It does not matter\n" +
            "                which lines\n" +
            "            </li>\n" +
            "            <li><b>Line Coverage Challenge</b>: Cover the specified line or at least one more branch if\n" +
            "                available\n" +
            "            </li>\n" +
            "            <li><b>Mutation Test Challenge</b>: Write a test or improve an existing one to kill the\n" +
            "                generated mutant.\n" +
            "            </li>\n" +
            "            <li><b>Smell Challenge</b>: Remove a code or test smell.\n" +
            "            </li>\n" +
            "        </ol>\n" +
            "        <li>Click on a <b>Challenge</b> to get the content and more details,</li>\n" +
            "        <li>The <i>branch</i> information states in which branch the Challenges was generated</li>\n" +
            "        <li>Challenges can also be solved in other branches if the mentioned code is available</li>\n" +
            "        <li>Challenges are only generated and solved during the <i>run/build</i> of a project/job in Jenkins\n" +
            "        </li>\n" +
            "        <li>Normally, the build is triggered after each commit or after a specific amount of time. If you\n" +
            "            don't want to wait, you can start the build manually by clicking <b>Build Now</b> in the left\n" +
            "            panel\n" +
            "        </li>\n" +
            "        <li>If the Challenge cannot be solved for some reason or the class/method under test should not be\n" +
            "            tested, the Challenges can be rejected\n" +
            "        </li>\n" +
            "        <li>To reject a Challenge, click the <b>Reject</b> button on each Challenge and add a reason, why the\n" +
            "            Challenge was rejected\n" +
            "        </li>\n" +
            "        <li>Challenges may be rejected automatically if they are not solvable anymore</li>\n" +
            "        <li>Rejected Challenges won't appear to be generated a second time</li>\n" +
            "        <li>After rejection a new Challenge is generated if the code of the project is available</li>\n" +
            "        <li>Hover over a rejected Challenge to see the reason for rejection</li>\n" +
            "\n" +
            "        <li>If a Class Coverage Challenge is rejected, the whole <i>class</i> is not considered again\n" +
            "            for new Challenges\n" +
            "        </li>\n" +
            "        <li>Rejected Class Coverage Challenges can be restored with the <b>Undo</b>-Button next to\n" +
            "            the Challenge\n" +
            "        </li>\n" +
            "        <li>The Challenge will not be immediately active again, it has to be generated again to appear</li>\n" +
            "\n" +
            "        <li>To store a Challenge, click the store button</li>\n" +
            "        <li>Stored Challenges cannot be completed and will count as rejected should they be solved</li>\n" +
            "        <li>To view, unshelve and send stored Challenges press the Stored Challenges Button</li>\n" +
            "        <li>Stored Challenges can be sent to another participant if that option is enabled</li>\n" +
            "\n" +
            "        <li>If there are no Challenges generated for you, although you have committed something recently,\n" +
            "            then check the <b>Git Names</b> in your profile settings\n" +
            "        </li>\n" +
            "        <li>In addition to Challenges, <b>Quests</b> are generated and can be solved step by step,\n" +
            "            since they consist of different Challenges. The Challenges of a quest must be solved\n" +
            "            in the given order\n" +
            "        </li>\n" +
            "\n" +
            "        <li>Also have a look at the <b>Achievements</b> available in your profile. You can access them\n" +
            "            by clicking on the link of your name in the Leaderboard. Remember to choose the right project\n" +
            "            in the dropdown in your profile.\n" +
            "        </li>\n" +
            "        <li>You can also view the Achievements of other players by clicking on their name in the leaderboard\n" +
            "        </li>\n" +
            "\n" +
            "        <li>The <b>Avatar</b> displayed in the leaderboard can be changed in your profile settings.\n" +
            "            There are 50 different avatars you can choose from\n" +
            "        </li>\n" +
            "        <li>When a build for example solves a Challenge or generates a new one, notifications are sent to\n" +
            "            the given email address. You can disable this option in your profile settings\n" +
            "        </li>\n" +
            "    </ul>"

}

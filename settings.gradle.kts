pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 카카오 sdk 설정
        maven {
            url = uri("https://devrepo.kakao.com/nexus/content/groups/public/")
        }
    }

    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
    }

}

rootProject.name = "Application Scalp_care"
include(":app")

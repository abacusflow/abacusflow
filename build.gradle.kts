plugins {
    id("abacusflow-base")
}

group = "org.abacusflow"


tasks.register("installGitHooks") {
    group = "git"
    description = "安装 Git hook 脚本到 .git/hooks 目录"

    doLast {
        val script = file(".githook/pre-push-check-format.sh")
        val target = file(".git/hooks/pre-push")

        target.writeText(script.readText())
        target.setExecutable(true)

        println("✅ 已安装 pre-push hook 脚本")
    }
}


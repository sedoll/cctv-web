const blockedPrefixes = ['/community']
const blockedExactPaths = new Set(['/login', '/signup'])

export default defineNuxtRouteMiddleware((to) => {
    const isBlockedPrefix = blockedPrefixes.some((prefix) =>
        to.path === prefix || to.path.startsWith(`${prefix}/`),
    )

    if (isBlockedPrefix || blockedExactPaths.has(to.path)) {
        return navigateTo('/')
    }
})
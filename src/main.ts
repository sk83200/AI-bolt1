@@ .. @@
 // Navigation guards for authentication
 router.beforeEach((to, from, next) => {
   const authStore = useAuthStore()
   
   // Check if route requires authentication
   if (to.meta.requiresAuth && !authStore.isAuthenticated) {
     next({ name: 'Login' })
   } 
   // Check if route requires specific role
   else if (to.meta.roles && !authStore.hasRole(to.meta.roles)) {
     next({ name: 'Dashboard' })
   }
   // If trying to access login/register while authenticated
   else if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
     next({ name: 'Dashboard' })
   } 
   else {
     next()
   }
 })
/*
 * Package for fetchProfile storage
 * After user logged into the system, RBAC module should singleton Profile Pool
 * 1) Based join User - Role ( Calculated User Profile )
 * 2) Based join Group - Role ( Calculated Group Profile )
 * 3) The data structure should be:
 * {
 *     user:"xxx",
 *     role:[{
 *          priority: xxx,
 *          roleId: "xxx"
 *     }],
 *     group:[{
 *          priority: xxx,
 *          groupId: "xxx",
 *          role:[{
 *              priority: xxx,
 *              roleId: "xxx"
 *          }]
 *     }],
 *     "fetchProfile":{
 *          "PROFILE1":["PERMISSION1","PERMISSION2"],
 *          "PROFILE2":["PERMISSION2","PERMISSION4"]
 *     }
 * }
 */
package io.zerows.extension.module.rbac.component.authorization;
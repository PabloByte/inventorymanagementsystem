package com.springweb.catalog.bootstrap;



/* 

@Component
public class BackfillRunner implements  CommandLineRunner {

         private final InventoryAdminService adminService;

  public BackfillRunner(InventoryAdminService adminService) {
    this.adminService = adminService;
  }

  @Override
  public void run(String... args) {
    // Ejecutar SOLO si se pasa el flag -Dbackfill=true o SPRING_BACKFILL=true
    boolean doBackfill = Boolean.parseBoolean(System.getProperty("backfill", "false"))
                        || Boolean.parseBoolean(System.getenv("SPRING_BACKFILL"));
    if (!doBackfill) return;

    adminService.backfillMainFromProductStock("admin");
    System.out.println("[BACKFILL] Completado.");
  }

}

*/
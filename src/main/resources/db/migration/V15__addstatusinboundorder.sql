-- Ampliar el CHECK de status para incluir PARTIALLYDELIVERY
-- Nota: mantenemos los estados existentes y agregamos el nuevo.
BEGIN;

ALTER TABLE public.inbound_orders
  DROP CONSTRAINT IF EXISTS inbound_orders_status_check;

ALTER TABLE public.inbound_orders
  ADD CONSTRAINT inbound_orders_status_check
  CHECK (
    status IN (
      'PENDING',
      'RECEIVED',
      'CANCELLED',
      'PARTIAL_DELIVERY'   -- si ya lo tenías, lo preservamos  -- nuevo valor que causaba el error
    )
  );

COMMIT;

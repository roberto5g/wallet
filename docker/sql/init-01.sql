-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	"name" varchar(100) NOT NULL,
	tax_id varchar(14) NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- public.wallets definition

-- Drop table

-- DROP TABLE public.wallets;

CREATE TABLE public.wallets (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	user_id uuid NOT NULL,
	balance numeric(15, 2) DEFAULT 0.00 NOT NULL,
	status varchar(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT wallets_balance_check CHECK ((balance >= (0)::numeric)),
	CONSTRAINT wallets_pkey PRIMARY KEY (id),
	CONSTRAINT wallets_user_id_key UNIQUE (user_id)
);


-- public.wallets foreign keys

ALTER TABLE public.wallets ADD CONSTRAINT wallets_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;

-- public.transactions definition

-- Drop table

-- DROP TABLE public.transactions;

CREATE TABLE public.transactions (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	wallet_id uuid NOT NULL,
	amount numeric(15, 2) NOT NULL,
	"type" varchar(20) NOT NULL,
	related_transaction_id uuid NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT transactions_pkey PRIMARY KEY (id),
	CONSTRAINT transactions_type_check CHECK (((type)::text = ANY ((ARRAY['DEPOSIT'::character varying, 'WITHDRAWAL'::character varying, 'TRANSFER_OUT'::character varying, 'TRANSFER_IN'::character varying])::text[])))
);
CREATE INDEX idx_transactions_created_at ON public.transactions USING btree (created_at);
CREATE INDEX idx_transactions_wallet ON public.transactions USING btree (wallet_id);


-- public.transactions foreign keys

ALTER TABLE public.transactions ADD CONSTRAINT fk_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES public.wallets(id) ON DELETE CASCADE;
ALTER TABLE public.transactions ADD CONSTRAINT transactions_wallet_id_fkey FOREIGN KEY (wallet_id) REFERENCES public.wallets(id) ON DELETE CASCADE;



INSERT INTO public.users
(id, "name", tax_id, created_at, updated_at)
VALUES('7fec14cd-09b1-4d05-be31-c679a32b54df'::uuid, 'Roberto G santos', '00000000001', '2025-08-02 00:13:36.388', '2025-08-02 00:13:36.388');
INSERT INTO public.users
(id, "name", tax_id, created_at, updated_at)
VALUES('dcfe55ad-1955-471a-88eb-3632edb1401e'::uuid, 'Carlos Abreu', '00000000002', '2025-08-02 00:19:31.010', '2025-08-02 00:19:31.010');
INSERT INTO public.users
(id, "name", tax_id, created_at, updated_at)
VALUES('1d14403b-2f41-46f6-976f-b44f357f9564'::uuid, 'Bod', '1111111111111', '2025-08-03 18:58:21.472', '2025-08-03 18:58:21.472');
-- Massa de dados inicial para demonstrar as regras de negocio do desafio:
-- distribuicao por decada, por fabricante, contagem de nao vendidas e "ultima semana".
-- 'created' e definido explicitamente para simular cadastros antigos e recentes.

INSERT INTO aeronave (nome, marca, ano, descricao, vendido, created, updated) VALUES
('E2-190',   'Embraer', 2014, 'Jato comercial de fuselagem estreita para curtas e medias distancias', true,  now() - interval '40 days', now() - interval '40 days'),
('KC-390',   'Embraer', 2015, 'Cargueiro militar de transporte tatico',                              false, now() - interval '25 days', now() - interval '25 days'),
('737-100',  'Boeing',  1998, 'Jato comercial de fuselagem estreita, geracao classica',              false, now() - interval '20 days', now() - interval '20 days'),
('747-400',  'Boeing',  1992, 'Jato comercial de fuselagem larga de longo alcance',                  true,  now() - interval '15 days', now() - interval '15 days'),
('A320',     'Airbus',  1995, 'Jato comercial de fuselagem estreita',                                true,  now() - interval '10 days', now() - interval '10 days'),
('A350',     'Airbus',  2016, 'Jato comercial de fuselagem larga de longo alcance',                  false, now() - interval '3 days',  now() - interval '3 days'),
('Global 7500','Gulfstream', 2018, 'Jato executivo de longo alcance',                                 false, now() - interval '2 days',  now() - interval '2 days'),
('CRJ-900',  'Bombardier', 2008, 'Jato regional',                                                     true,  now() - interval '1 days',  now() - interval '1 days'),
('Citation X','Cessna',   2003, 'Jato executivo de medio porte',                                      false, now(),                       now());

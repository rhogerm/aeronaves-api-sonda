CREATE TABLE aeronave (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    marca       VARCHAR(100) NOT NULL,
    ano         INTEGER NOT NULL,
    descricao   TEXT,
    vendido     BOOLEAN NOT NULL DEFAULT FALSE,
    created     TIMESTAMP NOT NULL DEFAULT now(),
    updated     TIMESTAMP NOT NULL DEFAULT now(),

    -- Reforca em nivel de banco a mesma whitelist aplicada na camada de validacao da API
    -- (com.sonda.aeronaves.model.Fabricante), evitando inconsistencias mesmo em inserts diretos.
    CONSTRAINT chk_aeronave_marca CHECK (
        marca IN (
            'Embraer', 'Boeing', 'Airbus', 'Bombardier', 'Cessna', 'ATR',
            'Gulfstream', 'Dassault', 'Lockheed Martin', 'Piper',
            'Textron Aviation', 'Saab', 'Antonov', 'De Havilland'
        )
    ),
    CONSTRAINT chk_aeronave_ano CHECK (ano BETWEEN 1903 AND 2100)
);

CREATE INDEX idx_aeronave_marca ON aeronave (marca);
CREATE INDEX idx_aeronave_ano ON aeronave (ano);
CREATE INDEX idx_aeronave_created ON aeronave (created);
